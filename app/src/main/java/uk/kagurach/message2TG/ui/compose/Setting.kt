package uk.kagurach.message2TG.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import uk.kagurach.message2TG.util.LogUtil

class Setting(
  private val rowModifier: Modifier = Modifier.fillMaxWidth(),
  private val nameModifier: Modifier = Modifier,
  private val nameTextStyle: TextStyle = TextStyle.Default,
  private val descriptionModifier: Modifier = Modifier,
  private val descriptionTextStyle: TextStyle = TextStyle.Default,
  private val switchModifier: Modifier = Modifier
) {

  @Composable
  fun BooleanSetting(
    name: String,
    initialState: Boolean = true,
    description: String? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null
  ) {
    var checkState by remember { mutableStateOf(initialState) }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = rowModifier
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(columnMaxWidthPercent)
      ) {
        Text(
          text = name,
          modifier = nameModifier,
          style = nameTextStyle
        )
        Spacer(modifier = defaultVerticalSpacerModifier)
        Text(
          text = description ?: "",
          modifier = descriptionModifier,
          style = descriptionTextStyle
        )
      }
      Spacer(modifier = Modifier.weight(1f))
      Switch(
        checked = checkState,
        onCheckedChange = {
          checkState = it
          onCheckedChange?.invoke(it)
        },
        modifier = switchModifier
      )
    }
  }

  @Composable
  fun StringSetting(
    name: String,
    initialState: String = "",
    description: String? = null,
    onValueUpdate: ((String) -> Unit)? = null
  ) {
    var value by remember { mutableStateOf(initialState) }
      Column(
        modifier = rowModifier
      ) {
        Text(
          text = name,
          modifier = nameModifier,
          style = nameTextStyle
        )
        Spacer(modifier = defaultVerticalSpacerModifier)
        Text(
          text = description ?: "",
          modifier = descriptionModifier,
          style = descriptionTextStyle
        )
        Spacer(modifier = defaultVerticalSpacerModifier)
        TextField(
          value = value,
          maxLines = 1,
          singleLine = true,
          modifier = Modifier.fillMaxWidth(1f),
          visualTransformation = VisualTransformation.None,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
          onValueChange = { v ->
            val trimmed = v.trim()
            value = trimmed
            onValueUpdate?.invoke(trimmed)
           }
        )
      }
  }

  companion object {
    @JvmStatic
    val columnMaxWidthPercent = 0.8f
    @JvmStatic
    val defaultVerticalSpacerModifier = Modifier.size(5.dp)
  }
}