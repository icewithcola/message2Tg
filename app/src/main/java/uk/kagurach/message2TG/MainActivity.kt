package uk.kagurach.message2TG

import android.Manifest.permission
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import uk.kagurach.tgbotapi.BotApiImpl

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Check Permission
    if (ContextCompat.checkSelfPermission(this, permission.READ_SMS)
      != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        this,
        permission.RECEIVE_SMS
      )
      != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(permission.READ_SMS, permission.RECEIVE_SMS), 1001
      )
    }
    val storage = Storage(baseContext)
    var token: String = ""
    var chatId: Long = 0
    storage.getDefaults { s, l ->
      token = s?:""
      chatId = l?:0
    }

    setContent {
      Column {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding()
        )
        SettingPage(baseContext,token,chatId)
      }
    }
  }
}

@Composable
fun SettingPage(ctx: Context,defaultToken: String,defaultChatId: Long) {
  var token by remember {
    mutableStateOf(defaultToken)
  }
  var chatId by remember {
    mutableLongStateOf(defaultChatId)
  }

  var botTokenTextColor by remember {
    mutableStateOf(Color.Black)
  }

  val rowStyle = Modifier
    .fillMaxWidth()
    .padding(vertical = 20.dp, horizontal = 20.dp)


  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Row(modifier = rowStyle) {
      TextField(
        value = token,
        onValueChange = { token = it.trimIndent() },
        label = { Text(text = "Bot Token") },
        maxLines = 1,
        textStyle = TextStyle(
          fontFamily = FontFamily.Monospace,
          color = botTokenTextColor
        ),
        modifier = Modifier
          .weight(1f)
          .horizontalScroll(rememberScrollState())
      )

      IconButton(
        onClick = {
          val clipboard = getSystemService(ctx, ClipboardManager::class.java)
          if (clipboard == null ||
            clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) != true
          ) {
            return@IconButton
          }
          val pasteData = clipboard.primaryClip?.getItemAt(0)
          if (pasteData != null) {
            token = pasteData.text.toString().split('\n')[0].trimIndent()
          }
          botTokenTextColor = Color.Black
        },
        modifier = Modifier.size(56.dp)
      ) {
        Icon(
          painter = painterResource(R.drawable.content_paste),
          contentDescription = "paste"
        )
      }
    }

    Row(modifier = rowStyle) {
      TextField(
        value = if (chatId == 0L) {
          ""
        } else chatId.toString(),
        onValueChange = { newInput ->
          if (newInput.isEmpty()) {
            chatId = 0
            return@TextField
          }

          var result = ""
          if (newInput.startsWith("-")) {
            result += "-"
          }
          newInput.forEach {
            if (it in '0'..'9') {
              result += it
            }
          }
          chatId = result.toLong()
        },
        label = { Text(text = "Chat Id") },
        maxLines = 1,
        textStyle = TextStyle(
          fontFamily = FontFamily.Monospace,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier
          .weight(1f)
          .horizontalScroll(rememberScrollState())
      )
      IconButton(
        onClick = {
          if (token.isEmpty()){
            return@IconButton
          }
          Toast.makeText(ctx,"Send message to your bot",Toast.LENGTH_SHORT).show()
          val botApiImpl = BotApiImpl(token, chatId)
          botApiImpl.getUpdates(
            limit = 1,
            timeout = 5,
            errorHandler = { err->
              if (err.code() == 401 || err.code() == 404){ // Failed To authenticate or wrong token
                botTokenTextColor = Color.Red
              }
            }
          ) { result ->
            if (result.isNotEmpty()) {
              val gotId = result[0].message?.chat?.id ?: 0
              if (chatId == 0L && gotId != 0L) {
                chatId = gotId
              }
              botTokenTextColor = Color.Black
            }
          }
        },
        modifier = Modifier.size(56.dp)
      ) {
        Icon(
          painter = painterResource(R.drawable.track_changes),
          contentDescription = "paste"
        )
      }
    }
    Button(onClick = {
      val botApiImpl = BotApiImpl(token, chatId)
      val storage = Storage(ctx)
      botApiImpl.sendMessage(text = "Message2Tg Connected successfully") { result ->
        storage.setDefaults(token, chatId)
        testAndStartService(ctx,true)
      }
    }) {
      Text(text = "Go!")
    }
  }
}