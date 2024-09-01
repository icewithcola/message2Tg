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
import uk.kagurach.message2TG.ui.compose.Setting

@Composable
fun AdvancedSettings(context: Context) {
  val setting = Setting(
    rowModifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 8.dp),
    nameTextStyle = TextStyle(
      fontSize = 24.sp,
      fontWeight = FontWeight(400),
      fontFamily = FontFamily.SansSerif
    ),
    descriptionModifier = Modifier.padding(start = 2.dp)
  )



  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(0.8f)
      .verticalScroll(rememberScrollState())
  ) {
    setting.BooleanSetting(
      name = "I set something",
      description = "Yes"
    )

    setting.BooleanSetting(
      name = "I set something too",
      description = "Noooooo"
    )
  }
}