package uk.kagurach.message2TG

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class BotStorage(private val context: Context) {
  private val defaultToken = stringPreferencesKey("default_token")
  private val defaultChatId = longPreferencesKey("default_chatId")

  fun getDefaults(processData: ((String?, Long?) -> Unit)? = null) {
    runBlocking {
      val job = launch {
        val tokenDeferred = async {
          val flow = context.dataStore.data
            .map { value ->
              value[defaultToken]
            }
          flow.first()
        }
        val chatIdDeferred = async {
          val flow = context.dataStore.data
            .map { value ->
              value[defaultChatId]
            }
          flow.first()
        }

        val token = tokenDeferred.await()
        val chatId = chatIdDeferred.await()

        processData?.invoke(token, chatId)
      }

      job.join()
    }
  }

  fun setDefaults(token: String, chatId: Long) {
    CoroutineScope(Dispatchers.Default).launch {
      context.dataStore.edit { value ->
        value[defaultToken] = token
        value[defaultChatId] = chatId
      }
    }
  }
}