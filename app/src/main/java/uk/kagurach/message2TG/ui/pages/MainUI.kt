package uk.kagurach.message2TG.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.kagurach.message2TG.ui.theme.ThemeWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI(context: Context, token: String, chatId: Long) {
  ThemeWrapper {
    Surface {
      var showBottomSheet by remember { mutableStateOf(false) }
      val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
      )

      Column {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
        )

        MainPage(context, token, chatId)

        Row {
          Spacer(modifier = Modifier.fillMaxWidth(0.78f))
          FloatingActionButton(
            onClick = { showBottomSheet = true },
            modifier = Modifier.offset(y = (-15).dp)
          ) {
            Icon(Icons.Filled.Settings, "Open application settings.")
          }
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
        )
      }

      if (showBottomSheet) {
        ModalBottomSheet(
          modifier = Modifier.fillMaxHeight(),
          sheetState = sheetState,
          onDismissRequest = { showBottomSheet = false }
        ) {
          AdvancedSettings(context)
        }
      }
    }
  }
}