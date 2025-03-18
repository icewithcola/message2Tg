package uk.kagurach.message2TG.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.Telephony.Sms
import uk.kagurach.message2TG.R


/**
 * System Utilities.
 */
object SystemHelper {
  /**
   * Reads the latest SMS messages from the device's inbox.
   *
   * @param context The context used to access the content resolver.
   * @param count The maximum number of messages to retrieve (default is 10).
   * @return A list of pairs containing the sender's phone number and message body.
   */
  fun readMessage(context: Context, count: Int = 10): List<Pair<String, String>> {
    val result = mutableListOf<Pair<String, String>>()

    val cursor = context.contentResolver.query(
      Sms.CONTENT_URI,
      arrayOf(Sms.ADDRESS, Sms.BODY), // Only query necessary columns for efficiency
      null, null, "${Sms.DATE} DESC LIMIT $count" // Order by date and limit results
    )

    cursor?.use {
      if (it.moveToFirst()) { // Ensure there's at least one result
        do {
          val address = it.getString(it.getColumnIndexOrThrow(Sms.ADDRESS))
          val body = it.getString(it.getColumnIndexOrThrow(Sms.BODY))
          result.add(address to body)
        } while (it.moveToNext() && result.size < count)
      }
    } ?: loge("ReadMessage", "Failed to query SMS content")

    return result
  }

  /**
   * Get battery percentage.
   *
   * @param context The context used to access the content resolver.
   * @return Battery remain, like 99.0%
   */
  fun getBatteryLevel(context: Context): String {
    val batteryStatus =
      context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    val batteryPct = level * 100 / scale.toFloat()

    return "${context.getString(R.string.battery_level)}: $batteryPct%"
  }
}