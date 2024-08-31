package uk.kagurach.message2TG

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import uk.kagurach.tgbotapi.BotApiImpl

class NewMessageHandler : BroadcastReceiver() {
  private lateinit var botApiImpl: BotApiImpl

  override fun onReceive(context: Context?, intent: Intent?) {
    if (context == null) {
      return
    }
    botApiImpl = BotApiImpl(context)

    if (intent == null || intent.action != "android.provider.Telephony.SMS_RECEIVED") {
      Log.i("NewMessageHandler", "Called by unknown or null intent?")
      return
    }

    val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
    if (messages.isEmpty()) {
      return
    }

    val messageText = StringBuilder()
    val sender = messages[0].originatingAddress
    messages.forEach { message ->
      if (message.originatingAddress == sender) {
        messageText.append(message.messageBody)
      }
    }
    // TODO: Add Message filter and Template
    botApiImpl.sendMessage(text = "From ${sender}:\n${messageText}")
  }
}