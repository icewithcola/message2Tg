package uk.kagurach.message2TG.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import uk.kagurach.message2TG.R
import uk.kagurach.message2TG.SettingStorage
import uk.kagurach.message2TG.diagnose.DiagnoseHelper
import uk.kagurach.message2TG.ui.compose.Setting

@Composable
fun AdvancedSettings(context: Context) {
  val setting = Setting(
    rowModifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 10.dp),
    nameTextStyle = TextStyle(
      fontSize = 24.sp,
      fontWeight = FontWeight(400),
      fontFamily = FontFamily.SansSerif
    ),
    descriptionModifier = Modifier.padding(start = 2.dp)
  )

  val settingStorage = SettingStorage(context)
  var diagnoseMessage by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(0.8f)
      .padding(horizontal = 8.dp)
      .verticalScroll(rememberScrollState())
  ) {
    setting.BooleanSetting(
      name = ContextCompat.getString(context, R.string.silent_test),
      description = ContextCompat.getString(context, R.string.silent_test_dscr),
      initialState = settingStorage.get(settingStorage.sendSilentMessageOnTest) == true
    ) { settingStorage.set(settingStorage.sendSilentMessageOnTest, it) }

    setting.BooleanSetting(
      name = ContextCompat.getString(context, R.string.extract_code),
      description = ContextCompat.getString(context, R.string.extra_code_dscr),
      initialState = settingStorage.get(settingStorage.extractVerifyCode) == true
    ) { settingStorage.set(settingStorage.extractVerifyCode, it) }

    setting.BooleanSetting(
      name = context.getString(R.string.use_foreground_service),
      description = context.getString(R.string.use_foreground_service_dscr),
      initialState = settingStorage.get(settingStorage.useForegroundService) == true
    ) { settingStorage.set(settingStorage.useForegroundService, it) }

    setting.BooleanSetting(
      name = context.getString(R.string.silent_in_night),
      description = context.getString(R.string.silent_in_night_dscr),
      initialState = settingStorage.get(settingStorage.silentInNight) == true
    ) { settingStorage.set(settingStorage.silentInNight, it) }

    setting.BooleanSetting(
      name = context.getString(R.string.use_command),
      description = context.getString(R.string.use_command_dscr),
      initialState = settingStorage.get(settingStorage.useCommand) != false
    ) { settingStorage.set(settingStorage.useCommand, it) }

    setting.BooleanSetting(
      name = context.getString(R.string.battery_notification),
      description = context.getString(R.string.battery_notification_dscr),
      initialState = settingStorage.get(settingStorage.batteryNotification) == true
    ) { settingStorage.set(settingStorage.batteryNotification, it) }

    TextButton(
      modifier = Modifier.padding(start = 5.dp), onClick = {
        diagnoseMessage =
          DiagnoseHelper.diagnose(context).joinToString() +
          DiagnoseHelper.buildInfo().joinToString()
      }) {
      Text(text = stringResource(R.string.diagnose))
    }

    Text(text = diagnoseMessage, fontSize = 14.sp, color = Color.Gray, modifier = Modifier)

  }
}