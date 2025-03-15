package uk.kagurach.message2TG

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.kagurach.message2TG.util.formatMessage
import uk.kagurach.message2TG.util.logi
import uk.kagurach.tgbotapi.BotApiImpl
import java.util.Calendar

class NewMessageHandler : BroadcastReceiver() {

  override fun onReceive(context: Context?, intent: Intent?) {
    if (context == null || intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
      logi("NewMessageHandler", "Received unknown or null intent")
      return
    }

    val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
    if (messages.isEmpty()) {
      return
    }

    val sender = messages[0].originatingAddress ?: "Unknown"
    val messageText = messages.joinToString("") { it.messageBody }

    val botApiImpl = BotApiImpl(context) // 局部初始化，避免 `lateinit`
    val settingStorage = SettingStorage(context)

    // 计算是否应该静音
    val disableNotification = shouldDisableNotification(settingStorage)

    // 在 IO 线程执行网络请求，避免阻塞主线程
    CoroutineScope(Dispatchers.IO).launch {
      botApiImpl.sendMessage(
        text = formatMessage(context, sender, messageText),
        disableNotification = disableNotification,
        parseMode = "MarkdownV2"
      )
    }
  }

  /**
   * 判断当前时间是否属于静音时段
   */
  private fun shouldDisableNotification(settingStorage: SettingStorage): Boolean {
    if (settingStorage.get(settingStorage.silentInNight) != true) {
      return false
    }

    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)

    val startHour = 23
    val startMinute = 0
    val endHour = 6
    val endMinute = 30

    @Suppress("KotlinConstantConditions")
    return (currentHour > startHour || (currentHour == startHour && currentMinute >= startMinute)) ||
      (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute))
  }
}
