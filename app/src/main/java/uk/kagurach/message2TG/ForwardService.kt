package uk.kagurach.message2TG

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.ServiceCompat
import uk.kagurach.message2TG.util.LogUtil.logi

class ForwardService : Service() {
  companion object {
    var isStarted = false
  }

  private var wakeLock: PowerManager.WakeLock? = null

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  private fun acquireWakeLock() {
    val powerManager = getSystemService(POWER_SERVICE) as PowerManager
    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "message2TG:WakeLock")
    wakeLock?.acquire(10 * 60 * 1000L /* 10 minutes */)
  }

  private fun releaseWakeLock() {
    wakeLock?.release()
    wakeLock = null
  }

  private fun startForegroundService() {
    val notification = Notification.Builder(this, "FOREGROUND_SERVICE")
      .setContentTitle("Forward Service Running")
      .setContentText("Listening for new SMS messages")
      .build()

    ServiceCompat.startForeground(
      this,
      104,
      notification,
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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

    // 1. 监听短信广播
    val receiver = NewMessageHandler()
    val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)
    } else {
      registerReceiver(receiver, intentFilter)
    }

    isStarted = true

    // 2. 获取设置信息，决定是否开启前台服务
    val settingStorage = SettingStorage(baseContext)
    if (settingStorage.get(settingStorage.useForegroundService) == true) {
      startForegroundService()
    }

    // 3. 获取 WakeLock 保持 CPU 运行
    acquireWakeLock()

    // 4. 把后台接受指令消息打开
    CommandWorker.scheduleOneTimeWork(baseContext)

    return START_STICKY
  }

  override fun onLowMemory() {
    logi("ForwardService", "Low System Memory - Trying to Restart")
    restartService()
  }

  override fun onDestroy() {
    logi("ForwardService", "Service Destroyed")
    releaseWakeLock()
    restartService()
    super.onDestroy()
  }

  private fun restartService() {
    startForegroundService(Intent(applicationContext, ForwardService::class.java))
  }
}
