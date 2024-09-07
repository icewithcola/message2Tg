package uk.kagurach.message2TG.util

import android.content.Context
import uk.kagurach.message2TG.R
import uk.kagurach.message2TG.SettingStorage

// Make the result always acceptable to API
fun String.promiseValue(): String {
  return this.replace("-", "\\-")
}

fun formatMessage(context: Context, sender: String, text: String): String {
  val settingStorage = SettingStorage(context)
  val code = if (settingStorage.get(settingStorage.extractVerifyCode) == true) {
    extractVerifyCode(text)
  } else null

  val sb = StringBuilder()

  // Sender part
  sb.append("${context.getString(R.string.sender)} ${sender.promiseValue()} \n")

  // Optional: Code
  if (code != null) {
    sb.append("${context.getString(R.string.verification_code)} `${code.promiseValue()}`\n")
  }

  // Original text
  sb.append("${context.getString(R.string.original_text)}\n>${text.promiseValue()}")


  return sb.toString()
}