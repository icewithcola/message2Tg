package uk.kagurach.message2TG

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.kagurach.message2TG.util.CommandHandler
import uk.kagurach.message2TG.util.loge
import uk.kagurach.message2TG.util.logi
import uk.kagurach.tgbotapi.BotApiImpl
import uk.kagurach.tgbotapi.typeadapter.Update
import java.util.concurrent.TimeUnit

class CommandWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
  companion object {
    const val TAG = "CommandWorker"

    fun scheduleOneTimeWork(context: Context) {
      val settingStorage = SettingStorage(context)
      val useCommand = settingStorage.get(settingStorage.useCommand) != false
      if (!useCommand) { return } // Function disabled

      val workRequest = OneTimeWorkRequestBuilder<CommandWorker>()
        .setInitialDelay(90, TimeUnit.SECONDS) // 1.5 分钟后执行
        .setConstraints(
          Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        )
        .build()

      WorkManager.getInstance(context).enqueueUniqueWork(
        TAG,
        ExistingWorkPolicy.APPEND_OR_REPLACE,
        workRequest
      )
    }
  }

  /**
   * Background worker that fetches updates from the bot API and processes commands.
   *
   * @return Result indicating success or failure.
   */
  override fun doWork(): Result {
    val botApiImpl = BotApiImpl(applicationContext)
    logi(TAG, "Routine Started, listening to chatId: ${BotApiImpl.defaultChatId}.")

    try {
      botApiImpl.getUpdates(
        onSuccess = { updateList ->
          if (updateList.isNotEmpty()) {
            handleUpdate(updateList.last(), botApiImpl)
          }
        },
        onHttpError = { e ->
          Log.e(TAG, "doWork: HttpException: ${e.stackTraceToString()}")
        },
      )
    } catch (e: Exception) {
      loge(TAG, "Exception in doWork: ${e.stackTraceToString()}")
    }

    // Schedule next execution
    scheduleOneTimeWork(applicationContext)
    return Result.success()
  }

  /**
   * Handles a single update from the bot API.
   *
   * @param update The update received from the bot API.
   * @param botApiImpl Instance of the bot API for sending messages.
   */
  private fun handleUpdate(update: Update, botApiImpl: BotApiImpl) {
    val message = update.message ?: return
    val sender = message.chat.id
    val updateId = update.updateId
    val storage = CommandWorkerStorage(applicationContext)
    val lastUpdate = storage.get(storage.lastUpdate)

    if (sender != BotApiImpl.defaultChatId) {
      logi(TAG, "Ignoring message from chatId: $sender (Expected: ${BotApiImpl.defaultChatId})")
      return
    }

    if (lastUpdate == updateId) {
      logi(TAG, "Duplicate update detected: $updateId")
      return
    }

    storage.set(storage.lastUpdate, updateId)
    logi(TAG, "Processing new update: $updateId from chatId: $sender")

    CommandHandler.processCommand(message.text.orEmpty(), botApiImpl, applicationContext)
  }
}

val Context.commandWorkerStorage: DataStore<Preferences> by preferencesDataStore(name = "command_worker")

class CommandWorkerStorage(private val context: Context) {
  val lastUpdate = longPreferencesKey("last_update")

  fun <T> get(key: Preferences.Key<T>): T? =
    runBlocking {
      context.commandWorkerStorage.data
        .map { value ->
          value[key]
        }
        .first()
    }

  fun <T> set(key: Preferences.Key<T>, value: T) =
    CoroutineScope(Dispatchers.Default).launch {
      context.commandWorkerStorage.edit {
        it[key] = value
      }
    }
}