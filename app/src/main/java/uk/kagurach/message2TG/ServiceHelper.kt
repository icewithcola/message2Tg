package uk.kagurach.message2TG

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import uk.kagurach.message2TG.util.LogUtil.logi
import uk.kagurach.tgbotapi.BotApiImpl

/** testAndStartService
 * @param skipChatIdCheck if true, only check the bot token with getMe()
 */
fun testAndStartService(context: Context, skipChatIdCheck: Boolean = false,startForegroundService: Boolean = false) {
  if (!skipChatIdCheck) {
    if (!validateBotToken(context)) {
      logi("ServiceHelper", "Bot token is wrong")
      return
    }
  }

  if (startForegroundService){
    val intent = Intent(context,ForwardService::class.java)
    context.startForegroundService(intent)
  }else {
    context.startService(Intent(context, ForwardService::class.java))
  }
}

@Suppress("unused")
fun validateChatId(
  context: Context,
  text: String? = null
): Boolean {
  val botApiImpl = BotApiImpl(context)
  var result = false
  botApiImpl.sendMessage(
    text = text ?: ContextCompat.getString(context, R.string.connect_success),
    onSuccess = {
      if (it.text == text) {
        result = true
      }
    }
  )
  return result
}

fun validateBotToken(
  context: Context
): Boolean {
  val botApiImpl = BotApiImpl(context)
  var result = false
  botApiImpl.getMe {
    result = it.ok
  }
  return result
}