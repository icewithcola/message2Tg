package uk.kagurach.message2TG

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import uk.kagurach.message2TG.util.formatMessage
import uk.kagurach.message2TG.util.logi
import uk.kagurach.tgbotapi.BotApiImpl
import java.util.Calendar

class NewMessageHandler : BroadcastReceiver() {
  private lateinit var botApiImpl: BotApiImpl

  override fun onReceive(context: Context?, intent: Intent?) {
    if (context == null) {
      return
    }
    botApiImpl = BotApiImpl(context)


    if (intent == null || intent.action != "android.provider.Telephony.SMS_RECEIVED") {
      logi("NewMessageHandler", "Called by unknown or null intent?")
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
    val settingStorage = SettingStorage(context)
    botApiImpl.sendMessage(
      text = formatMessage(context, sender.toString(), messageText.toString()),
      disableNotification = if (settingStorage.get(settingStorage.silentInNight) == true){
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Define the start and end times
        val startHour = 23
        val startMinute = 0
        val endHour = 6
        val endMinute = 30

        if (currentHour == startHour && currentMinute >= startMinute) {
          true
        } else if (currentHour > startHour) {
          true
        }

        // Check if the time is between 00:00 and 6:30
        if (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute)) {
          true
        }

        false
      } else false,
      parseMode = "MarkdownV2"
    )
  }
}