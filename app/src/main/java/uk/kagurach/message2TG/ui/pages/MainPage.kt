package uk.kagurach.message2TG.ui.pages

import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.kagurach.message2TG.BotStorage
import uk.kagurach.message2TG.R
import uk.kagurach.message2TG.SettingStorage
import uk.kagurach.message2TG.testAndStartService
import uk.kagurach.message2TG.ui.compose.Inputer
import uk.kagurach.tgbotapi.BotApiImpl
import uk.kagurach.tgbotapi.validateBotToken
import uk.kagurach.tgbotapi.validatePartialBotToken

@Composable
fun MainPage(ctx: Context, defaultToken: String, defaultChatId: Long) {
  var token by remember { mutableStateOf(defaultToken) }
  var chatId by remember { mutableLongStateOf(defaultChatId) }
  var tokenVisibility by remember { mutableStateOf(false) }

  val settingStorage = SettingStorage(ctx)

  val inputer = Inputer(
    inputTextStyle = TextStyle(
      fontFamily = FontFamily.Monospace,
    ),
    textFieldSharedModifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 15.dp, horizontal = 20.dp),
    iconModifier = Modifier
      .size(56.dp)
  )

  val keyboardController = LocalSoftwareKeyboardController.current
  val focusManager = LocalFocusManager.current

  Column(
    modifier = Modifier
      .fillMaxHeight(0.9f)
      .fillMaxWidth()
      .clickable(
        interactionSource = remember {
          MutableInteractionSource()
        },
        indication = null
      ) {
        keyboardController?.hide()
        focusManager.clearFocus(force = true)
      },
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    inputer.InputBox(
      value = token,
      labelText = getString(ctx, R.string.bot_token),
      iconPainter = painterResource(R.drawable.content_paste),
      onValueChange = {
        if (it.isEmpty() || validatePartialBotToken(it))
          token = it
      },
      iconButtonOnClick = {
        val clipboard = getSystemService(ctx, ClipboardManager::class.java)
        if (clipboard == null ||
          clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) != true
        ) {
          return@InputBox
        }
        val pasteData = clipboard.primaryClip?.getItemAt(0)
        if (pasteData != null) {
          val data = pasteData.text.toString().split('\n')[0].trimIndent()
          if (validateBotToken(data)) {
            token = data
          } else {
            Toast.makeText(ctx, getString(ctx, R.string.no_token_in_clipboard), Toast.LENGTH_SHORT)
              .show()
          }
        }
      },
      onFocusChanged = { focusState ->
        tokenVisibility = focusState.hasFocus
      },
      visualTransformation = if (tokenVisibility)
        VisualTransformation.None
      else
        PasswordVisualTransformation()
    )

    inputer.InputBox(
      value = if (chatId == 0L) "" else chatId.toString(),
      labelText = getString(ctx, R.string.chat_id),
      iconPainter = painterResource(R.drawable.track_changes),
      keyboardType = KeyboardType.Decimal,
      onValueChange = { newInput ->
        if (newInput.isEmpty() || newInput == "-") {
          chatId = 0
        } else if (newInput.matches("^(-|)\\d{0,16}\$".toRegex())) {
          chatId = newInput.toLong()
        }
      },
      iconButtonOnClick = {
        if (token.isEmpty()) {
          return@InputBox
        }
        Toast.makeText(ctx, getString(ctx, R.string.send_message_to_bot), Toast.LENGTH_SHORT).show()
        val botApiImpl = BotApiImpl(token, chatId)
        botApiImpl.getUpdates(
          limit = 1,
          timeout = 10,
          onHttpError = { e ->
            CoroutineScope(Dispatchers.Main).launch {
              Toast.makeText(
                ctx, getString(
                  ctx, when (e.code()) {
                    400 -> R.string.need_start_bot
                    401, 404 -> R.string.wrong_bot_token
                    else -> throw e
                  }
                ), Toast.LENGTH_SHORT
              ).show()
            }
          },
          onSuccess = { result ->
            if (result.isNotEmpty()) {
              val gotId = result[0].message?.chat?.id ?: 0
              if (chatId == 0L && gotId != 0L) {
                chatId = gotId
              }
            }
          }
        )
      }
    )

    Button(onClick = {
      val botApiImpl = BotApiImpl(token, chatId)
      val botStorage = BotStorage(ctx)
      botApiImpl.sendMessage(
        text = getString(ctx, R.string.connect_success),
        disableNotification = settingStorage.get(settingStorage.sendSilentMessageOnTest),
        onHttpError = { e ->
          CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
              ctx, getString(
                ctx, when (e.code()) {
                  400 -> R.string.need_start_bot
                  401, 404 -> R.string.wrong_bot_token
                  else -> throw e
                }
              ), Toast.LENGTH_SHORT
            ).show()
          }
        },
        onSuccess = {
          botStorage.setDefaults(token, chatId)
          testAndStartService(ctx, true)
        }
      )
    }) {
      Text(text = getString(ctx, R.string.start_service))
    }
  }
}