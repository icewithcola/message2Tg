package uk.kagurach.message2TG

import android.content.Context
import android.provider.Telephony.Sms
import android.util.Log
import uk.kagurach.message2TG.util.loge

/**
 * Reads the latest SMS messages from the device's inbox.
 *
 * @param ctx The context used to access the content resolver.
 * @param count The maximum number of messages to retrieve (default is 10).
 * @return A list of pairs containing the sender's phone number and message body.
 */
fun readMessage(ctx: Context, count: Int = 10): List<Pair<String, String>> {
  val result = mutableListOf<Pair<String, String>>()

  val cursor = ctx.contentResolver.query(
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
