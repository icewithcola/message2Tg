package uk.kagurach.message2TG

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat

class ForwardService : Service() {
  companion object {
    var isStarted = false
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  private fun startForeground(){
    val notification = Notification.Builder(this,"FOREGROUND_SERVICE")
      .build()
    ServiceCompat.startForeground(
      this,
      104,
      notification,
      if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
      } else {
        0
      }
    )
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (isStarted) {
      return START_STICKY_COMPATIBILITY
    }
    if (Build.VERSION.SDK_INT > VERSION_CODES.TIRAMISU) {
      registerReceiver(
        NewMessageHandler(), IntentFilter("android.provider.Telephony.SMS_RECEIVED"),
        RECEIVER_EXPORTED
      )
    } else {
      registerReceiver(
        NewMessageHandler(), IntentFilter("android.provider.Telephony.SMS_RECEIVED"),
      )
    }
    isStarted = true
    val settingStorage = SettingStorage(baseContext)
    if (settingStorage.get(settingStorage.useForegroundService) == true){
      startForeground()
    }
    return START_STICKY
  }

  override fun onLowMemory() {
    Log.i("message2TG","Low System Memory")
    stopSelf()
    super.onLowMemory()
  }
}