package uk.kagurach.message2TG

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val Context.settingStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingStorage(private val context: Context) {
  val sendSilentMessageOnTest = booleanPreferencesKey("send_silent_message_on_test")

  fun <T> get(key: Preferences.Key<T>): T? =
    runBlocking {
      context.settingStore.data
        .map { value ->
          value[key]
        }
        .first()
    }

  fun <T> set(key: Preferences.Key<T>, value: T) =
    CoroutineScope(Dispatchers.Default).launch {
      context.settingStore.edit {
        it[key] = value
      }
    }
}