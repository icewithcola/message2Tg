package uk.kagurach.tgbotapi

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import uk.kagurach.message2TG.BotStorage
import uk.kagurach.message2TG.util.loge
import uk.kagurach.tgbotapi.typeadapter.Message
import uk.kagurach.tgbotapi.typeadapter.MessageReturned
import uk.kagurach.tgbotapi.typeadapter.Update
import uk.kagurach.tgbotapi.typeadapter.UpdatesReturned
import uk.kagurach.tgbotapi.typeadapter.User
import uk.kagurach.tgbotapi.typeadapter.UserReturned
import java.util.concurrent.TimeUnit.SECONDS
import javax.net.ssl.SSLException
import kotlin.properties.Delegates

class BotApiImpl {
  companion object {
    const val BASE_URL = "https://api.telegram.org/"
    const val TAG = "BotApiImpl"

    // Retry counts
    var retryCount = 0
    const val MAX_RETRY = 3

    var initDefaults = false
    lateinit var defaultToken: String
    var defaultChatId by Delegates.notNull<Long>()
  }

  constructor(ctx: Context) {
    val botStorage = BotStorage(ctx)
    botStorage.getDefaults { s, l ->
      if (s != null && l != null) {
        defaultToken = s
        defaultChatId = l
        initDefaults = true
      } else {
        loge(TAG, "Cannot read token and chatId from storage")
      }
    }
  }

  constructor(token: String, chatId: Long) {
    defaultToken = token
    defaultChatId = chatId
    initDefaults = true
  }

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, SECONDS)
    .readTimeout(60, SECONDS)
    .writeTimeout(60, SECONDS)
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
    onHttpError: ((HttpException) -> Unit)? = null,
    onFailure: ((UserReturned) -> Unit)? = null,
    onSuccess: ((User) -> Unit)? = null,
    onFinished: ((UserReturned) -> Unit)? = null,
  ) {
    checkArgument(token)

    scope.launch {
      val response: UserReturned
      try {
        response = service.getMe(token ?: defaultToken)
      } catch (exception: HttpException) {
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
    onHttpError: ((HttpException) -> Unit)? = null,
    onFailure: ((MessageReturned) -> Unit)? = null,
    onSuccess: ((Message) -> Unit)? = null,
    onFinished: ((MessageReturned) -> Unit)? = null,
  ) {
    checkArgument(token)

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
      } catch (exception: HttpException) {
        if (onHttpError != null) {
          onHttpError.invoke(exception)
        } else {
          loge(TAG, "retrofit2.HttpException:\n${exception.stackTrace}")
        }
        return@launch
      } catch (exception: SSLException) {
        loge(TAG, "SSLException:\n${exception.stackTrace}")
        if (retryCount <= MAX_RETRY) {
          retryCount++
          delay(40 * retryCount * 1000.toLong())
          sendMessage(token, chatId, text, disableNotification, parseMode, onHttpError)
        }
        return@launch
      }
      if (response.ok) {
        onSuccess?.invoke(response.result)
      } else {
        onFailure?.invoke(response)
      }

      onFinished?.invoke(response)
      retryCount = 0
    }
  }

  fun getUpdates(
    token: String? = null,
    offset: Int? = null,
    limit: Int? = null,
    timeout: Int? = null,
    onHttpError: ((HttpException) -> Unit)? = null,
    onFailure: ((UpdatesReturned) -> Unit)? = null,
    onSuccess: ((List<Update>) -> Unit)? = null,
    onFinished: ((UpdatesReturned) -> Unit)? = null,
  ) {
    checkArgument(token)

    scope.launch {
      val response: UpdatesReturned
      try {
        response = service.getUpdates(token ?: defaultToken, offset, limit, timeout)
      } catch (exception: HttpException) {
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

  private fun checkArgument(token: String?): Unit =
    if (!validateArgNotNullOrHasDefault(initDefaults, token)
    ) {
      throw RuntimeException("this function should be called either use default value or give every parameter")
    } else { }
}