package uk.kagurach.message2TG

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.kagurach.message2TG.util.LogUtil.logi
import uk.kagurach.message2TG.util.formatMessage
import uk.kagurach.tgbotapi.BotApiImpl
import uk.kagurach.tgbotapi.ParseMode
import java.security.MessageDigest
import java.util.Calendar

class NewMessageHandler : BroadcastReceiver() {

  companion object{
    @JvmStatic
    private val TAG = "NewMessageHandler"
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    if (context == null || intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
      logi(TAG, "Received unknown or null intent")
      return
    }

    val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
    if (messages.isEmpty()) {
      return
    }

    val sender = messages[0].originatingAddress ?: "Unknown"
    val messageText = messages.joinToString("") { it.messageBody }
    val messageTime = messages[0].timestampMillis

    // 计算消息哈希值
    val messageHash = hashMessage(sender, messageText, messageTime)
    val sharedPreferences = context.getSharedPreferences("message_prefs", Context.MODE_PRIVATE)

    // 检查是否已处理过该消息
    if (sharedPreferences.getString("last_message_hash", "") == messageHash) {
      logi(TAG, "Duplicate message detected, ignoring")
      return
    }

    // 记录新的消息哈希值
    sharedPreferences.edit { putString("last_message_hash", messageHash) }

    val botApiImpl = BotApiImpl(context) // 局部初始化，避免 `lateinit`
    val settingStorage = SettingStorage(context)

    // 计算是否应该静音
    val disableNotification = shouldDisableNotification(settingStorage)

    // 在 IO 线程执行网络请求，避免阻塞主线程
    CoroutineScope(Dispatchers.IO).launch {
      botApiImpl.sendMessage(
        text = formatMessage(context, sender, messageText),
        disableNotification = disableNotification,
        parseMode = ParseMode.MARKDOWN
      )
    }

    // 最后顺便把 CommandWorker 唤醒一下
    CommandWorker.scheduleOneTimeWork(context)
  }

  /**
   * 计算消息的哈希值，防止重复发送
   */
  private fun hashMessage(sender: String, message: String, messageTime: Long): String {
    val input = "$sender:$message:$messageTime"
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
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