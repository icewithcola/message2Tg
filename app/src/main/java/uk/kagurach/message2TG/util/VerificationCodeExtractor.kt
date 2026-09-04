package uk.kagurach.message2TG.util

import android.content.Context
import android.text.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
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
        if (!punctuationMatch.isNullOrBlank()) {
            return punctuationMatch
        }

        val numberMatch = numberOnlyCode.find(text)?.value
        if (!numberMatch.isNullOrBlank()) {
            return numberMatch
        }

        return null
    }

    suspend fun extract(text: String): String? {
        val localResult = extractLocally(text)

        if (TextUtils.isEmpty(openAIEndpoint) ||
            TextUtils.isEmpty(openAIKey) ||
            TextUtils.isEmpty(openAIModel)
        ) {
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

    private suspend fun extractWithAI(text: String, localCandidate: String?): String? =
        withContext(Dispatchers.IO) {
            try {
                val promptContent = StringBuilder().run {
                    if (!localCandidate.isNullOrEmpty()) {
                        append("My system has already found a possible candidate:")
                        append(localCandidate)
                        append(". Please verify if this is the correct code or if there is a better one in the message")
                        append('\n')
                    }
                    append("Message to analyze:")
                    append(text)
                    toString()
                }

                val jsonBody = JSONObject().run {
                    put("model", openAIModel)
                    put("stream", false)
                    put("messages", JSONArray().run {
                        put(JSONObject().run {
                            put("role", "system")
                            put("content", SYSTEM_PROMPT)
                        })
                        put(JSONObject().run {
                            put("role", "user")
                            put("content", promptContent)
                        })
                    })
                }

                val requestBody = jsonBody
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
                        LogUtil.loge(
                            "VerificationCodeExtractor",
                            "API Error: ${response.code} ${response.message}"
                        )
                        return@withContext null
                    }

                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val content = jsonResponse.optJSONArray("choices")
                            ?.optJSONObject(0)
                            ?.optJSONObject("message")
                            ?.optString("content")

                        return@withContext content?.substringAfter("<CODE>")
                            ?.substringBefore("</CODE>")
                    }
                }
            } catch (e: Exception) {
                LogUtil.loge("VerificationCodeExtractor", "Exception: ${e.message}")
                e.printStackTrace()
            }

            return@withContext null
        }

    companion object {
        val SYSTEM_PROMPT = """        
            You are an expert at extracting verification codes from text messages.
            Analyze the following message and extract the verification code.
            The verification code is typically a 4-8 digit number, but can also be alphanumeric.
            Return ONLY the code inside <CODE>...</CODE> tags.
            If no code is found, return an empty <CODE></CODE> tag.
        """.trimIndent()
    }
}

