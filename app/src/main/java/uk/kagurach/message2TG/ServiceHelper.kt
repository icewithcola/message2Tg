package uk.kagurach.message2TG

import android.content.Context
import android.content.Intent
import uk.kagurach.tgbotapi.BotApiImpl

fun testAndStartService(context: Context,skipCheck: Boolean = false){
  if (!skipCheck) {
    if (!isConfigOkay(context)) {
      return
    }
  }
  context.startService(Intent(context, ForwardService::class.java))
}

fun isConfigOkay(context: Context,text: String = "Connected to Telegram, running as service"): Boolean{
  val botApiImpl = BotApiImpl(context)
  var result = false
  botApiImpl.sendMessage(
    text = text,
  ){ if (it.text == text){
      result = true
    }
  }
  return result
}