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
  private val client = OkHttpClient()
  private val prompt = if (LocaleListCompat.getAdjustedDefault()[0]?.language == "zh") PROMPT_CN else PROMPT_EN

  /**
   * Build a Telegram message from the given content.
   */
  suspend fun buildTelegramMessage(context: Context, sender: String, text: String): String? =
    makeRequest(text)?.let { intent ->
      buildString {
        append("[${intent.realIntent}]")
        append("${context.getString(R.string.sender)} <a href=\"tel:${sender}\">${sender.escapeForTelegram()}</a>\n")
        intent.verificationCode?.let {
          append("${context.getString(R.string.verification_code)} <code>${it.escapeForTelegram()}</code>\n")
        }
        intent.tldr?.let { append(it) }
        append("${context.getString(R.string.original_text)}\n")
        append("<blockquote>")
        append(text.escapeForTelegram())
        append("</blockquote>")
      }
    }


  private suspend fun makeRequest(text: String): MessageIntent? = withContext(Dispatchers.IO) {
    try {
      val json = JSONObject()
      json.put("model", openAIModel)
      json.put("stream", false)
      json.put("max_tokens", 16384)

      val messages = JSONArray()

      val systemMessage = JSONObject()
      systemMessage.put("role", "system")
      systemMessage.put("content", prompt)
      messages.put(systemMessage)

      val userMessage = JSONObject()
      userMessage.put("role", "user")
      userMessage.put("content", text)
      messages.put(userMessage)

      json.put("messages", messages)

      val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
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
      LogUtil.loge(TAG, "Exception: ${e.message}")
      e.printStackTrace()
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
2. **Language Anchor**: All text fields (`realIntent`, `tldr`) MUST be written in [English].
3. **Strict Output**: Output ONLY the raw JSON string. Do not include any conversational filler, markdown code blocks (e.g., ```json), or post-processing notes.

# Output Schema (Mapping to Kotlin `MessageIntent`)
{
  "realIntent": "String - Concise intent (e.g., Login Auth, Delivery, Marketing, Fraud Alert, Account Risk). Limit to 3 words.",
  "tldr": "String? - One-line summary. For OTPs, specify the service (e.g., 'GitHub verification code'). For marketing/spam, this field MUST be null.",
  "verificationCode": "String? - Extract 4-8 character alphanumeric code. Return null if not present."
}

# Safety Policy
As a technical parsing utility, do not lecture users on security. However, you must fulfill your "Audit Role" by flagging potential phishing, malicious links, or social engineering attempts.
    """

    private const val PROMPT_CN = """
# Role
你是一个专为 Android 系统底层集成的“短信语义解析引擎” (SMS Semantic Parser)。你的任务是将接收到的原始短信文本，根据预定义的 Kotlin Data Class 结构，转换为标准化的 JSON 数据。

# Task Constraints
1. **意图识别**：精准判断发件人目的。如果是诈骗或骚扰，请在 realIntent 中直接标注。
2. **语言锁定**：所有的字段内容（realIntent, tldr）必须使用【中文】。
3. **输出纯净度**：仅输出标准的 JSON 字符串，严禁包含任何前导解释、Markdown 代码块标识符（如 ```json）或结尾说明。

# Output Schema (Mapping to Kotlin `MessageIntent`)
{
  "realIntent": "String - 简短意图（如：登录验证、快递取件、营销广告、疑似诈骗、异常登录），控制在 5 个汉字以内",
  "tldr": "String? - 一句话总结。验证码需说明平台（如：GitHub 的验证码）；若是营销广告，此字段必须返回 null",
  "verificationCode": "String? - 提取 4-8 位数字或字母验证码。若无则返回 null"
}

# Safety Policy
你作为一个技术解析组件，不对验证码本身进行安全性评价，但必须履行“风险标注”职责，识别恶意扣费、钓鱼链接等电信诈骗行为。

    """
  }
}