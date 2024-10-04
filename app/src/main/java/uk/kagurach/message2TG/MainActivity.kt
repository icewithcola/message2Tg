package uk.kagurach.message2TG

import android.Manifest.permission
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import uk.kagurach.message2TG.ui.pages.MainUI

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Check Permission
    val permissions = mutableListOf(permission.READ_SMS, permission.RECEIVE_SMS)
    if (VERSION.SDK_INT >= VERSION_CODES.P) {
      permissions.addAll(arrayOf(permission.FOREGROUND_SERVICE))
      if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU){
        permissions.addAll(arrayOf(permission.POST_NOTIFICATIONS))
        if (VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE) {
          permissions.addAll(arrayOf(permission.FOREGROUND_SERVICE_DATA_SYNC))
        }
      }
    }

    permissions.forEach {
      if (ContextCompat.checkSelfPermission(this, it)
        != PackageManager.PERMISSION_GRANTED
      ) {
        ActivityCompat.requestPermissions(
          this, permissions.toTypedArray(), 1001
        )
      }
    }

    // Make notification channel
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationChannel = NotificationChannel(
      "FOREGROUND_SERVICE",
      getString(R.string.notification_channel_dscr),
      NotificationManager.IMPORTANCE_DEFAULT
    )
    notificationManager.createNotificationChannel(notificationChannel)

    val botStorage = BotStorage(baseContext)
    var token: String = ""
    var chatId: Long = 0
    botStorage.getDefaults { s, l ->
      token = s ?: ""
      chatId = l ?: 0
    }

    setContent {
      MainUI(context = baseContext, token = token, chatId = chatId)
    }
  }
}

