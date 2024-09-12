package uk.kagurach.message2TG

import android.Manifest.permission
import android.content.pm.PackageManager
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
    val permissions = arrayOf(permission.READ_SMS, permission.RECEIVE_SMS)
    permissions.forEach {
      if (ContextCompat.checkSelfPermission(this, it)
        != PackageManager.PERMISSION_GRANTED
      ) {
        ActivityCompat.requestPermissions(
          this, permissions, 1001
        )
      }
    }

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

