package uk.kagurach.tgbotapi

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import uk.kagurach.message2TG.Storage
import uk.kagurach.tgbotapi.typeadapter.Message
import uk.kagurach.tgbotapi.typeadapter.MessageReturned
import uk.kagurach.tgbotapi.typeadapter.Update
import uk.kagurach.tgbotapi.typeadapter.UpdatesReturned
import uk.kagurach.tgbotapi.typeadapter.UserReturned
import kotlin.properties.Delegates

class BotApiImpl {
  companion object {
    const val BASE_URL = "https://api.telegram.org/"

    var initDefaults = false
    lateinit var defaultToken: String
    var defaultChatId by Delegates.notNull<Long>()

    fun loge(msg: String?, func: String) { // For log errors only
      Log.e(
        "BotApiImpl", "$func ran failed " +
          if (!msg.isNullOrBlank()) {
            "with message $msg"
          } else {
            ""
          }
      )
    }

    fun logd(msg: String?, func: String) { // For log success only
      Log.d(
        "BotApiImpl", "$func ran success " +
          if (!msg.isNullOrBlank()) {
            "with message $msg"
          } else {
            ""
          }
      )
    }
  }

  constructor()
  constructor(ctx: Context) {
    val storage = Storage(ctx)
    storage.getDefaults { s, l ->
      if (s != null && l != null) {
        defaultToken = s
        defaultChatId = l
        initDefaults = true
      } else {
        Log.e("BotApiImpl", "Cannot read token and chatId from storage")
      }
    }
  }

  constructor(token: String, chatId: Long) {
    defaultToken = token
    defaultChatId = chatId
    initDefaults = true
  }

  private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()
  private val service: BotApiInterface = retrofit.create(BotApiInterface::class.java)
  private val scope = CoroutineScope(Dispatchers.IO)

  fun getMe(token: String? = null, resultHandler: ((UserReturned) -> Unit)? = null) {
    if (token == null && !initDefaults) {
      throw RuntimeException("this function should be called either use default value or give every parameter")
    }

    scope.launch {
      val response = service.getMe(token ?: defaultToken)

      if (response.ok) {
        // Set checked token
        logd(response.result.toString(), ::getMe.name)
      } else {
        loge(null, ::getMe.name)
      }
      // At last we handle result
      resultHandler?.invoke(response)
    }
  }

  fun sendMessage(
    token: String? = null,
    chatId: Long? = null,
    text: String,
    errorHandler: ((retrofit2.HttpException) -> Unit)? = null,
    resultHandler: ((Message) -> Unit)? = null
  ) {
    if ((token == null || chatId == null) && !initDefaults) {
      throw RuntimeException("this function should be called either use default value or give every parameter")
    }

    scope.launch {
      val realToken = token ?: defaultToken
      val realChatId = chatId ?: defaultChatId

      var response: MessageReturned? = null
      try {
        response = service.sendMessage(realToken, realChatId, text)
      } catch (e: retrofit2.HttpException) {
        if (errorHandler != null) {
          errorHandler.invoke(e)
        } else {
          loge(e.printStackTrace().toString(), ::sendMessage.name)
          return@launch
        }
      }
      if (response == null) {
        return@launch
      }
      if (response.ok) {
        logd(response.result.text, ::sendMessage.name)
      } else {
        loge(null, ::sendMessage.name)
      }
      resultHandler?.invoke(response.result)
    }
  }

  fun getUpdates(
    token: String? = null,
    offset: Int? = null,
    limit: Int? = null,
    timeout: Int? = null,
    errorHandler: ((retrofit2.HttpException) -> Unit)? = null,
    resultHandler: ((List<Update>) -> Unit)?
  ) {
    if (token == null && !initDefaults) {
      throw RuntimeException("this function should be called either use default value or give every parameter")
    }

    scope.launch {
      val result: UpdatesReturned
      try {
        result = service.getUpdates(token ?: defaultToken, offset, limit, timeout)
      } catch (e: retrofit2.HttpException) {
        if (errorHandler != null) {
          errorHandler.invoke(e)
        } else {
          loge(e.printStackTrace().toString(), ::getUpdates.name)
        }
        return@launch
      }
      if (result.ok) {
        resultHandler?.invoke(result.result)
      }
    }
  }
}