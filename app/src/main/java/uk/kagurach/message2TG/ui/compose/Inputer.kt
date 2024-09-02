package uk.kagurach.message2TG.ui.compose

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

/*** Inputer:
 * Basic Wrapped InputBox with a IconButton,
 * with shared styling and generic settings
 * @see TextField
 * @see IconButton
 * @author kagura
 */
class Inputer(
  val inputTextStyle: TextStyle,
  val textFieldSharedModifier: Modifier,
  val iconModifier: Modifier
) {

  @Composable
  fun InputBox(
    value: String,
    labelText: String,
    textStyle: TextStyle = inputTextStyle,
    iconPainter: Painter,
    iconDescription: String = "",
    onValueChange: (String) -> Unit,
    iconButtonOnClick: () -> Unit,
    onFocusChanged: (FocusState) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text
  ) {

    TextField(
      value = value,
      onValueChange = onValueChange,
      label = { Text(text = labelText) },
      textStyle = textStyle,
      modifier = textFieldSharedModifier
        .onFocusChanged { onFocusChanged.invoke(it) },
      visualTransformation = visualTransformation,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = keyboardType),
      trailingIcon = {
        IconButton(
          onClick = iconButtonOnClick,
          modifier = iconModifier
        ) {
          Icon(
            painter = iconPainter,
            contentDescription = iconDescription
          )
        }
      }
    )
  }
}