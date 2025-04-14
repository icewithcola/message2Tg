package uk.kagurach.message2TG.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import uk.kagurach.message2TG.R
import uk.kagurach.message2TG.SettingStorage
import uk.kagurach.tgbotapi.BotApiImpl

class BatteryReceiver: BroadcastReceiver() {
  companion object{
    @JvmStatic
    private val TAG = "BatteryReceiver"
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    if (context == null || intent == null){
      return
    }
    val settingStorage = SettingStorage(context)
    if (settingStorage.get(settingStorage.batteryNotification) != true){ // 没有启用此功能
      return
    }

    val botApiImpl = BotApiImpl(context)

    val notifyMessage = when (intent.action){
      Intent.ACTION_BATTERY_LOW -> {
        context.getString(R.string.low_battery) + SystemHelper.getBatteryLevel(context)
      }
      Intent.ACTION_POWER_CONNECTED ->{
        context.getString(R.string.charger_connected)
      }
      Intent.ACTION_POWER_DISCONNECTED->{
        context.getString(R.string.charger_disconnected)
      }
      else -> { return }
    }

    botApiImpl.sendMessage(text = notifyMessage)
  }
}