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
import uk.kagurach.message2TG.util.LogUtil.loge
import uk.kagurach.tgbotapi.typeadapter.Message
import uk.kagurach.tgbotapi.typeadapter.MessageReturned
import uk.kagurach.tgbotapi.typeadapter.Returned
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
    botStorage.getDefaults { token, chatId ->
      if (token != null && chatId != null) {
        defaultToken = token
        defaultChatId = chatId
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

  fun getMe(
    token: String? = null,
    onHttpError: ((HttpException) -> Unit) = {},
    onFailure: ((UserReturned) -> Unit) = {},
    onSuccess: ((User) -> Unit) = {},
    onFinished: ((UserReturned) -> Unit) = {},
  ) {
    checkArgument(token)

    scope.launch {
      val response: UserReturned
      try {
        response = service.getMe(token ?: defaultToken)
      } catch (exception: HttpException) {
        httpExceptionHandler(exception, onHttpError, exception.response()?.errorBody()?.string())
        return@launch
      }

      responseHandler(response, onFailure, onSuccess, onFinished)
    }
  }

  fun sendMessage(
    token: String? = null,
    chatId: Long? = null,
    text: String,
    disableNotification: Boolean? = null,
    parseMode: ParseMode = ParseMode.NONE,
    onHttpError: ((HttpException) -> Unit) = {},
    onFailure: ((MessageReturned) -> Unit) = {},
    onSuccess: ((Message) -> Unit) = {},
    onFinished: ((MessageReturned) -> Unit) = {},
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
          parseMode.getName()
        )
      } catch (exception: HttpException) {
        httpExceptionHandler(exception, onHttpError, exception.response()?.errorBody()?.string())
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

      responseHandler(response, onFailure, onSuccess, onFinished)

      retryCount = 0
    }
  }

  fun getUpdates(
    token: String? = null,
    offset: Int? = null,
    limit: Int? = null,
    timeout: Int? = null,
    onHttpError: ((HttpException) -> Unit) = {},
    onFailure: ((UpdatesReturned) -> Unit) = {},
    onSuccess: ((List<Update>) -> Unit) = {},
    onFinished: ((UpdatesReturned) -> Unit) = {},
  ) {
    checkArgument(token)

    scope.launch {
      val response: UpdatesReturned
      try {
        response = service.getUpdates(token ?: defaultToken, offset, limit, timeout)
      } catch (exception: HttpException) {
        httpExceptionHandler(exception, onHttpError, exception.response()?.errorBody()?.string())
        return@launch
      }

      responseHandler(response, onFailure, onSuccess, onFinished)
    }
  }

  private fun checkArgument(token: String?): Unit =
    if (!validateArgNotNullOrHasDefault(initDefaults, token)
    ) {
      throw RuntimeException("this function should be called either use default value or give every parameter")
    } else {
    }

  /**
   * Inline function for handling httpException
   */
  private inline fun httpExceptionHandler(
    exception: HttpException,
    onHttpError: ((HttpException) -> Unit),
    response: String? = null
  ) {
    loge(TAG, (exception.message.toString() + response))

    onHttpError.invoke(exception)
  }

  /**
   * Handles the API response based on the `Returned` interface.
   *
   * This function determines whether to invoke `onSuccess` or `onFailure` based on the `ok` status
   * of the response. The `onFinished` callback is always executed at the end.
   *
   * @param response The API response object that implements `Returned<Result>`, containing an `ok` status and a `result`.
   * @param onFailure Callback invoked when `response.ok` is `false`. Receives the `response` as a parameter. Default is an empty function.
   * @param onSuccess Callback invoked when `response.ok` is `true`. Receives the `result` of the response as a parameter. Default is an empty function.
   * @param onFinished Callback executed after processing the response, regardless of success or failure. Receives the `response` as a parameter. Default is an empty function.
   */
  private inline fun <reified Return, reified Result> responseHandler(
    response: Return,
    onFailure: ((Return) -> Unit),
    onSuccess: ((Result) -> Unit),
    onFinished: ((Return) -> Unit),
  )
    where Return : Returned<Result> {
    if (response.ok) {
      onSuccess.invoke(response.result)
    } else {
      onFailure.invoke(response)
    }

    onFinished.invoke(response)
  }
}