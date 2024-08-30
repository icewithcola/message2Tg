package uk.kagurach.message2TG

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.IBinder

class ForwardService : Service() {
  companion object {
    var isStarted = false
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
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
    return START_STICKY
  }

  override fun onLowMemory() {
    stopSelf()
    super.onLowMemory()
  }
}