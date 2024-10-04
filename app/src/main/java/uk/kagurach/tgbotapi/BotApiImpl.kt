package uk.kagurach.tgbotapi

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import uk.kagurach.message2TG.BotStorage
import uk.kagurach.tgbotapi.typeadapter.Message
import uk.kagurach.tgbotapi.typeadapter.MessageReturned
import uk.kagurach.tgbotapi.typeadapter.Update
import uk.kagurach.tgbotapi.typeadapter.UpdatesReturned
import uk.kagurach.tgbotapi.typeadapter.User
import uk.kagurach.tgbotapi.typeadapter.UserReturned
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.properties.Delegates

class BotApiImpl {
  companion object {
    const val BASE_URL = "https://api.telegram.org/"

    var initDefaults = false
    lateinit var defaultToken: String
    var defaultChatId by Delegates.notNull<Long>()
  }

  constructor()
  constructor(ctx: Context) {
    val botStorage = BotStorage(ctx)
    botStorage.getDefaults { s, l ->
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
  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60,SECONDS)
    .readTimeout(60,SECONDS)
    .writeTimeout(60,SECONDS)
    .build()
  private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .client(okHttpClient)
    .build()
  private val service: BotApiInterface = retrofit.create(BotApiInterface::class.java)
  private val scope = CoroutineScope(Dispatchers.IO)

  /** get...: Get the response and handle certain circumstances
   *  @param ... Are token or other things this function use
   *  @param onHttpError when getResponse throws HttpException, runs. If null, error the exception
   *  @param onFailure if response.ok == false, invokes with whole Returned value
   *  @param onSuccess if response.ok == true, invokes with result Object
   *  @param onFinished always invoke on the last line
   */
  fun getMe(
    token: String? = null,
    onHttpError: ((retrofit2.HttpException) -> Unit)? = null,
    onFailure: ((UserReturned) -> Unit)? = null,
    onSuccess: ((User) -> Unit)? = null,
    onFinished: ((UserReturned) -> Unit)? = null
  ) {

    if (!validateArgNotNullOrHasDefault(initDefaults, token)) {
      throw RuntimeException("this function should be called either use default value or give every parameter")
    }

    scope.launch {
      val response: UserReturned
      try {
        response = service.getMe(token ?: defaultToken)
      } catch (exception: retrofit2.HttpException) {
        if (onHttpError != null) {
          onHttpError.invoke(exception)
        } else {
          error(exception)
        }
        return@launch
      }

      // Invoke by result
      if (response.ok) {
        onSuccess?.invoke(response.result)
      } else {
        onFailure?.invoke(response)
      }

      // Invoke last
      onFinished?.invoke(response)
    }
  }

  fun sendMessage(
    token: String? = null,
    chatId: Long? = null,
    text: String,
    disableNotification: Boolean? = null,
    parseMode: String? = null,
    onHttpError: ((retrofit2.HttpException) -> Unit)? = null,
    onFailure: ((MessageReturned) -> Unit)? = null,
    onSuccess: ((Message) -> Unit)? = null,
    onFinished: ((MessageReturned) -> Unit)? = null
  ) {
    if (!validateArgNotNullOrHasDefault(initDefaults, token, chatId)) {
      throw RuntimeException("this function should be called either use default value or give every parameter")
    }

    scope.launch {
      val response: MessageReturned

      try {
        response = service.sendMessage(
          token ?: defaultToken,
          chatId ?: defaultChatId,
          text,
          disableNotification,
          parseMode
        )
      } catch (exception: retrofit2.HttpException) {
        if (onHttpError != null) {
          onHttpError.invoke(exception)
        } else {
          Log.e("BotApiImpl",exception.printStackTrace().toString())
        }
        return@launch
      }

      if (response.ok) {
        onSuccess?.invoke(response.result)
      } else {
        onFailure?.invoke(response)
      }

      onFinished?.invoke(response)
    }
  }

  fun getUpdates(
    token: String? = null,
    offset: Int? = null,
    limit: Int? = null,
    timeout: Int? = null,
    onHttpError: ((retrofit2.HttpException) -> Unit)? = null,
    onFailure: ((UpdatesReturned) -> Unit)? = null,
    onSuccess: ((List<Update>) -> Unit)? = null,
    onFinished: ((UpdatesReturned) -> Unit)? = null
  ) {
    if (!validateArgNotNullOrHasDefault(initDefaults, token)) {
      throw RuntimeException("this function should be called either use default value or give every parameter")
    }

    scope.launch {
      val response: UpdatesReturned
      try {
        response = service.getUpdates(token ?: defaultToken, offset, limit, timeout)
      } catch (exception: retrofit2.HttpException) {
        if (onHttpError != null) {
          onHttpError.invoke(exception)
        } else {
          error(exception)
        }
        return@launch
      }

      if (response.ok) {
        onSuccess?.invoke(response.result)
      } else {
        onFailure?.invoke(response)
      }

      onFinished?.invoke(response)
    }
  }
}