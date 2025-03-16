package uk.kagurach.message2TG.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import uk.kagurach.message2TG.R
import uk.kagurach.message2TG.readMessage
import uk.kagurach.tgbotapi.BotApiImpl
import uk.kagurach.tgbotapi.ParseMode

/**
 * Handles command parsing and execution.
 */
object CommandHandler {

  /**
   * Processes a command message and executes the corresponding action.
   *
   * @param command The raw command text.
   * @param botApiImpl Instance of the bot API for sending messages.
   * @param context Application context for accessing resources.
   */
  fun processCommand(command: String, botApiImpl: BotApiImpl, context: Context) {
    if (!command.startsWith("/")) return // Just Ignore
    when {
      command.startsWith("/getinfo", ignoreCase = true) -> handleGetInfo(botApiImpl, context)
      command.startsWith("/getLastMessage", ignoreCase = true) -> handleGetLastMessage(command, botApiImpl, context)
      else -> logi("CommandHandler", "Unknown command: $command")
    }
  }

  /**
   * Handles the "getinfo" command to retrieve battery status.
   *
   * @param botApiImpl Instance of the bot API for sending messages.
   * @param context Application context for accessing resources.
   */
  private fun handleGetInfo(botApiImpl: BotApiImpl, context: Context) {
    val batteryLevel = getBatteryLevel(context)
    botApiImpl.sendMessage(text = batteryLevel)
  }

  /**
   * Handles the "getLastMessage" command to fetch recent messages.
   *
   * @param command The raw command text (may include arguments).
   * @param botApiImpl Instance of the bot API for sending messages.
   * @param context Application context for accessing resources.
   */
  private fun handleGetLastMessage(command: String, botApiImpl: BotApiImpl, context: Context) {
    val args = command.split(" ")
    val messageCount = when (args.size) {
      1 -> 2
      2 -> args[1].toIntOrNull() ?: 2
      else -> 0
    }.coerceAtMost(5) // Max = 5 messages

    if (messageCount > 0) {
      val messages = readMessage(context, messageCount)
      if (messages.isNotEmpty()) {
        val responseText = messages
          .mapIndexed { idx, (sender, msg) -> "$idx:\n${context.getString(R.string.sender)}: `$sender` $msg" }
          .joinToString("\n")

        botApiImpl.sendMessage(text = responseText, parseMode = ParseMode.MARKDOWN)
      }
    }
  }

  private fun getBatteryLevel(context: Context): String {
    val batteryStatus =
      context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    val batteryPct = level * 100 / scale.toFloat()

    return "${context.getString(R.string.battery_level)}: $batteryPct%"
  }
}
