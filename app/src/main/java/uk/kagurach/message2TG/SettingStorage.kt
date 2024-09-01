package uk.kagurach.message2TG

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.settingStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingStorage {
}