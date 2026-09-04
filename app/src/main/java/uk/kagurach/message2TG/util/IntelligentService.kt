package uk.kagurach.message2TG.util

import android.content.Context
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import uk.kagurach.message2TG.R
import uk.kagurach.message2TG.SettingStorage
import uk.kagurach.message2TG.util.TelegramMarkdownEscaper.escapeForTelegram
import java.util.concurrent.TimeUnit

data class MessageIntent(
  val realIntent: String, // The intent of sender sending this message
  val tldr: String? = null,
  val verificationCode: String? = null
)

class IntelligentService(context: Context) {
  private val settingStorage = SettingStorage(context)
  private val openAIEndpoint = settingStorage.get(settingStorage.openAIEndpoint)
  private val openAIKey = settingStorage.get(settingStorage.openAIKey)
  private val openAIModel = settingStorage.get(settingStorage.openAIModel)
  private val client = OkHttpClient.Builder()
    .readTimeout(60, TimeUnit.SECONDS)
    .connectTimeout(800, TimeUnit.MILLISECONDS)
    .build()
  private val targetLanguage = LocaleListCompat.getAdjustedDefault()[0]?.language

  /**
   * Build a Telegram message from the given content.
   */
  suspend fun buildTelegramMessage(context: Context, sender: String, text: String): String? {
    val result = makeRequest(text) ?: return null
    val realIntent = result.realIntent
    val verificationCode = result.verificationCode
    val tldr = result.tldr

    return buildString {
      @Suppress("UselessCallOnNotNull")
      if (!realIntent.isNullOrBlank() || "null" == realIntent) {
        append("[${realIntent}] ")
      }

      if (!verificationCode.isNullOrBlank() || "null" == verificationCode) {
        append("${context.getString(R.string.verification_code)} <code>${verificationCode.escapeForTelegram()}</code>\n")
      }

      append("${context.getString(R.string.sender)} <a href=\"tel:${sender}\">${sender.escapeForTelegram()}</a>\n")

      if (!tldr.isNullOrBlank() || "null" == tldr) {
        append(tldr)
        append('\n')
      }

      append("${context.getString(R.string.original_text)}\n")
      append("<blockquote>")
      append(text.escapeForTelegram())
      append("</blockquote>")
    }
  }

  private suspend fun makeRequest(text: String): MessageIntent? = withContext(Dispatchers.IO) {
    try {
      val requestJson = JSONObject().run {
        put("model", openAIModel)
        put("stream", false)
        put("max_tokens", 16384)
        put("messages", JSONArray().run {
          put(JSONObject().run {
            put("role", "system")
            put("content", StringBuilder().run {
              append(PROMPT_EN)
              append("**IMPORTANT: USER LANGUAGE/LOCALE = ")
              append(targetLanguage)
            })
          })
          put(JSONObject().run {
            put("role", "user")
            put("content", text)
          })
        })
      }

      val requestBody = requestJson
        .toString()
        .toRequestBody("application/json".toMediaTypeOrNull())

      val request = Request.Builder()
        .url("$openAIEndpoint/v1/chat/completions")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer $openAIKey")
        .post(requestBody)
        .build()

      client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
          LogUtil.loge(TAG, "API Error: ${response.code} ${response.message}")
          return@withContext null
        }

        val responseBody = response.body?.string()
        if (responseBody != null) {
          val jsonResponse = JSONObject(responseBody)
          val content = jsonResponse.optJSONArray("choices")
            ?.optJSONObject(0)
            ?.optJSONObject("message")
            ?.optString("content")

          if (content != null) {
            val jsonContent = JSONObject(content)
            return@withContext MessageIntent(
              realIntent = jsonContent.optString("realIntent"),
              tldr = jsonContent.optString("tldr"),
              verificationCode = jsonContent.optString("verificationCode")
            )
          }
        }
      }
    } catch (e: Exception) {
      LogUtil.loge(TAG, "Exception: ${e.message}\n${e.stackTraceToString()}")
    }

    return@withContext null
  }

  companion object {
    private const val TAG = "IntelligentService"
    private const val PROMPT_EN = """
# Role
You are an "SMS Semantic Parsing Engine" designed for Android system-level integration. Your task is to transform raw SMS text into a standardized JSON object that maps directly to a Kotlin Data Class.

# Task Constraints
1. **Intent Analysis**: Identify the sender's true purpose. Label fraudulent or predatory messages clearly in the `realIntent` field.
2. **Language Anchor**: All text fields (`realIntent`, `tldr`) MUST be written in user's language.
3. **Strict Output**: Output ONLY the raw JSON string. Do not include any conversational filler, markdown code blocks (e.g., ```json), or post-processing notes.

# Output Schema
{
  "realIntent": "String - Concise intent (e.g., Login Auth, Delivery, Marketing, Fraud Alert, Account Risk). Limit to 3 words.",
  "tldr": "String? - One-line summary. For OTPs, specify the service (e.g., 'GitHub verification code'). For marketing/spam, this field MUST be null(not litural 'null').",
  "verificationCode": "String? - Extract 4-8 character alphanumeric code. Return null if not present."
}

## Example
```
[GitHub] Hi your verification code for your account is somecode.
```
with output
```json
{
  "realIntent": "OTP Code",
  "tldr": "Github OTP",
  "verificationCode": "somecode"
}
```

# Safety Policy
As a technical parsing utility, do not lecture users on security. However, you must fulfill your "Audit Role" by flagging potential phishing, malicious links, or social engineering attempts.

# i18n
You should reply in user's local language. For example, if the imput tells you user has `zh` locale, reply a `营销短信` as intent. Also the `tldr` should be in Chinese(Simplified).
    """
  }
}