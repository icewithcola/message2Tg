package uk.kagurach.message2TG.util

import android.content.Context
import uk.kagurach.message2TG.R
import uk.kagurach.message2TG.SettingStorage
import uk.kagurach.message2TG.util.TelegramMarkdownEscaper.escapeForTelegram

/**
 * Utility object for escaping Telegram HTML parsing
 */
object TelegramMarkdownEscaper {
  private val HTML_ESCAPE_CHARACTERS = mapOf(
    '<' to "&lt;",
    '>' to "&gt;",
    '&' to "&amp;"
  )

  fun String.escapeForTelegram(): String {
    return buildString(length) {
      for (char in this@escapeForTelegram) {
        if (char in HTML_ESCAPE_CHARACTERS.keys) {
          append(HTML_ESCAPE_CHARACTERS[char])
        } else {
          append(char)
        }
      }
    }
  }
}

/**
 * Message data class for better structure
 */
data class MessageContent(
  val sender: String,
  val text: String,
  val verificationCode: String? = null
)

/**
 * Formats message for Telegram with proper markdown escaping
 */
suspend fun formatMessage(context: Context, sender: String, text: String): String {
  val settingStorage = SettingStorage(context)
  if (settingStorage.get(settingStorage.advancedAITools) == true) {
    val intelligentService = IntelligentService(context)
    val result = intelligentService.buildTelegramMessage(context, sender, text)
    if (result != null) {
      return result
    }
  }

  // Get our verification code if enabled
  val verificationCode = if (settingStorage.get(settingStorage.extractVerifyCode) == true) {
    val extractor = VerificationCodeExtractor(context)
    extractor.extract(text)
  } else {
    null
  }

  val messageContent = MessageContent(
    sender = sender,
    text = text,
    verificationCode = verificationCode
  )

  return buildTelegramMessage(context, messageContent)
}

private fun buildTelegramMessage(context: Context, content: MessageContent): String {
  return buildString {
    // Sender information
    append("${context.getString(R.string.sender)} <a href=\"tel:${content.sender}\">${content.sender.escapeForTelegram()}</a>\n")

    // Verification code if available
    content.verificationCode?.let { code ->
      append("${context.getString(R.string.verification_code)} <code>${code.escapeForTelegram()}</code>\n")
    }

    // Original text
    append("${context.getString(R.string.original_text)}\n")
    append("<blockquote>")
    append(content.text.escapeForTelegram())
    append("</blockquote>")
  }
}