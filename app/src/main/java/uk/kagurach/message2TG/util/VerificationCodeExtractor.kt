package uk.kagurach.message2TG.util

import android.content.Context
import android.text.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import uk.kagurach.message2TG.SettingStorage

class VerificationCodeExtractor(context: Context) {

    private val settingStorage = SettingStorage(context)
    private val openAIEndpoint = settingStorage.get(settingStorage.openAIEndpoint)
    private val openAIKey = settingStorage.get(settingStorage.openAIKey)
    private val openAIModel = settingStorage.get(settingStorage.openAIModel)

    private val client = OkHttpClient()

    private val verifyCodeKeyWords = listOf(
        "验证码", "注册码", "校验码", "动态码", "动态密码",
        "Verify", "Code", "OTP", "Password"
    )
    private val numberOnlyCode = "\\d{4,8}".toRegex()
    private val byPunctuation = "：([0-9a-zA-Z-_]*)，".toRegex()

    private fun isVerifyCode(text: String): Boolean {
        for (verifyCodeKeyWord in verifyCodeKeyWords) {
            if (text.contains(verifyCodeKeyWord, true)) {
                return true
            }
        }
        return false
    }

    private fun extractLocally(text: String): String? {
        if (!isVerifyCode(text)) {
            return null
        }

        val punctuationMatch = byPunctuation.find(text)?.groupValues?.get(1)
        if (punctuationMatch != null && punctuationMatch.isNotBlank()) {
            return punctuationMatch
        }

        val numberMatch = numberOnlyCode.find(text)?.value
        if (numberMatch != null && numberMatch.isNotBlank()) {
            return numberMatch
        }

        return null
    }

    suspend fun extract(text: String): String? {
        val localResult = extractLocally(text)

        if (TextUtils.isEmpty(openAIEndpoint) || TextUtils.isEmpty(openAIKey) || TextUtils.isEmpty(openAIModel)) {
            return localResult
        }

        val aiResult = extractWithAI(
            text = text,
            localCandidate = localResult
        )

        return if (!aiResult.isNullOrBlank()) {
            LogUtil.logi("VerificationCodeExtractor", "Extracted code by AI")
            aiResult
        } else {
            LogUtil.logi("VerificationCodeExtractor", "Using local result as fallback")
            localResult
        }
    }

    private fun buildPrompt(text: String, localCandidate: String?): String {
        val intro = "You are an expert at extracting verification codes from text messages."
        val instruction = """
            Analyze the following message and extract the verification code.
            The verification code is typically a 4-8 digit number, but can also be alphanumeric.
            Return ONLY the code inside <CODE>...</CODE> tags.
            If no code is found, return an empty <CODE></CODE> tag.
        """.trimIndent()

        val context = if (localCandidate != null) {
            "My system has already found a possible candidate: '$localCandidate'. Please verify if this is the correct code or if there is a better one in the message. Return the single best code."
        } else {
            "My system could not find a code. Please analyze the full message to find it."
        }

        val message = """
            Message to analyze:
            $text
        """.trimIndent()

        return """
            $intro
            $instruction
            
            $context
            
            $message
            
            Remember, your final output must be ONLY the <CODE>...</CODE> tag.
        """.trimIndent()
    }

    private suspend fun extractWithAI(text: String, localCandidate: String?): String? = withContext(Dispatchers.IO) {
        try {
            val promptContent = buildPrompt(text, localCandidate)
                .replace("\n", "\\n")
                .replace("\"", "\\\"")

            val jsonBody = """
              {
                "model": "$openAIModel",
                "stream": false,
                "messages": [
                  {
                    "role": "user",
                    "content": "$promptContent"
                  }
                ],
                "max_tokens": 50
              }
            """.trimIndent()

            val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("$openAIEndpoint/v1/chat/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $openAIKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    LogUtil.loge("VerificationCodeExtractor", "API Error: ${response.code} ${response.message}")
                    return@withContext null
                }

                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    val content = jsonResponse.optJSONArray("choices")
                        ?.optJSONObject(0)
                        ?.optJSONObject("message")
                        ?.optString("content")

                    return@withContext content?.substringAfter("<CODE>")?.substringBefore("</CODE>")
                }
            }
        } catch (e: Exception) {
            LogUtil.loge("VerificationCodeExtractor", "Exception: ${e.message}")
            e.printStackTrace()
        }

        return@withContext null
    }
}

