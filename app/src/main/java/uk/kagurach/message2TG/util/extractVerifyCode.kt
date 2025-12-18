package uk.kagurach.message2TG.util

import android.content.Context
import android.text.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import uk.kagurach.message2TG.SettingStorage

val verifyCodeKeyWords = listOf(
  "验证码", "注册码", "校验码", "动态码", "动态密码",
  "Verify", "Code", "OTP", "Password"
)

val numberOnlyCode = "\\d{4,6}".toRegex()
val byPunctuation = "：([0-9a-zA-Z-_]*)，".toRegex()

fun isVerifyCode(text: String): Boolean {
  for (verifyCodeKeyWord in verifyCodeKeyWords) {
    if (text.contains(verifyCodeKeyWord, true)) {
      return true
    }
  }
  return false
}

suspend fun extractVerifyCode(text: String, context: Context): String? {
  val settingStorage = SettingStorage(context)
  val openAIEndpoint = settingStorage.get(settingStorage.openAIEndpoint)
  val openAIKey = settingStorage.get(settingStorage.openAIKey)
  val openAIModel = settingStorage.get(settingStorage.openAIModel)

  if (!TextUtils.isEmpty(openAIEndpoint) && !TextUtils.isEmpty(openAIKey) && !TextUtils.isEmpty(openAIModel) ){
    // Use AI to extract verify code
    val code = extractVerifyCodeWithAI(text, openAIEndpoint!!, openAIKey!!, openAIModel!!)
    if (!TextUtils.isEmpty(code)) {
      LogUtil.logi("extractVerifyCode", "Extracted code by AI")
      return code
    }
  }

  if (!isVerifyCode(text)) {
    return null
  }

  val numberMatch = numberOnlyCode.find(text)?.value
  val punctuationMatch = byPunctuation.find(text)?.groupValues?.get(1)

  if (punctuationMatch != null) {
    return punctuationMatch
  }
  if (numberMatch != null) {
    return numberMatch
  }

  return null
}

suspend fun extractVerifyCodeWithAI(text: String, endpoint: String, apiKey: String, model: String): String? = withContext(Dispatchers.IO) {
  try {
    val client = OkHttpClient()
    val content = """
      Extract the verification code from this message:
      $text
      If there is no verification code, return an empty string.
      You should only return the verification code, not the whole message.
      
      Example:
      OTP：123456，please enter the verification code in the app.
      Return:
      123456
    """.trimIndent()
      .replace("\n", "\\n")
      .replace("\"", "\\\"")

    val jsonBody = """
      {
        "model": "$model",
        "stream": false,
        "messages": [
          {
            "role": "user",
            "content": "$content"
          }
        ],
        "max_tokens": 1000
      }
    """.trimIndent()

    val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody)
    val request = Request.Builder()
      .url("$endpoint/v1/chat/completions")
      .addHeader("Content-Type", "application/json")
      .addHeader("Authorization", "Bearer $apiKey")
      .method("POST",requestBody)
      .build()
    
    client.newCall(request).execute().use { response ->
      if (!response.isSuccessful) {
        return@withContext null
      }
      
      val responseBody = response.body()?.string()
      if (responseBody != null) {
        val jsonResponse = JSONObject(responseBody)
        val code = jsonResponse.optJSONArray("choices")
          ?.optJSONObject(0)
          ?.optJSONObject("message")
          ?.optString("content")
        return@withContext code
      }
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }
  
  return@withContext null
}

