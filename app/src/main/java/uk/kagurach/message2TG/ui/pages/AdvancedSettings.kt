package uk.kagurach.message2TG.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import uk.kagurach.message2TG.R
import uk.kagurach.message2TG.SettingStorage
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
      initialState = settingStorage.get(settingStorage.sendSilentMessageOnTest) ?: false
    ) { settingStorage.set(settingStorage.sendSilentMessageOnTest, it) }

    setting.BooleanSetting(
      name = ContextCompat.getString(context, R.string.extract_code),
      description = ContextCompat.getString(context, R.string.extra_code_dscr),
      initialState = settingStorage.get(settingStorage.extractVerifyCode) ?: false
    ) { settingStorage.set(settingStorage.extractVerifyCode, it) }
  }
}