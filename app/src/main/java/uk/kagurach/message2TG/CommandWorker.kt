package uk.kagurach.message2TG

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkRequest
import android.os.BatteryManager
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import uk.kagurach.message2TG.util.CommandProcessor
import uk.kagurach.message2TG.util.logi
import uk.kagurach.tgbotapi.BotApiImpl
import java.util.concurrent.TimeUnit

class CommandWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
  companion object{
    const val TAG = "CommandWorker"

    fun scheduleOneTimeWork(context: Context) {
      val workRequest = OneTimeWorkRequestBuilder<CommandWorker>()
        .setInitialDelay(90, TimeUnit.SECONDS) // 1.5 分钟后执行
        .setConstraints(
          Constraints.Builder()
            .setRequiredNetworkRequest(NetworkRequest.Builder().build(), NetworkType.CONNECTED)
            .build()
        )
        .build()

      WorkManager.getInstance(context).enqueueUniqueWork(
        TAG,
        ExistingWorkPolicy.REPLACE,
        workRequest
      )
    }

  }

  override fun doWork(): Result {
    val botApiImpl = BotApiImpl(applicationContext)
    logi(TAG, "Routine Started")
    try {
      botApiImpl.getUpdates(onSuccess = { updateList ->
        if (!updateList.isEmpty() && updateList.first().message != null) {
          CommandProcessor.process(
            command = updateList.first().message?.text.orEmpty(),
            expect = "getinfo"
          ) {
            val batteryLevel = getBatteryLevel()
            botApiImpl.sendMessage(text = batteryLevel)
          }
        }
      })
    } catch (_: Exception) {
    }
    scheduleOneTimeWork(applicationContext) // 任务完成后安排下一次执行
    return Result.success()
  }

  private fun getBatteryLevel(): String {
    val batteryStatus =
      applicationContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    val batteryPct = level * 100 / scale.toFloat()

    return "${applicationContext.getString(R.string.battery_level)}: $batteryPct%"
  }
}