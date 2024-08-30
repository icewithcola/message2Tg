package uk.kagurach.message2TG

import android.content.Context
import android.provider.Telephony.Sms
import android.util.Log

fun ReadMessage(ctx: Context): List<String> {
  val cursor = ctx.contentResolver.query(
    Sms.CONTENT_URI,
    null, null, null
  )

  val result = mutableListOf<String>()

  if (cursor != null) {
    cursor.moveToFirst()

    do {
      val address = cursor.getString(cursor.getColumnIndexOrThrow(Sms.ADDRESS))
      val body = cursor.getString(cursor.getColumnIndexOrThrow(Sms.BODY))
      val id = cursor.getString(cursor.getColumnIndexOrThrow(Sms._ID))
      result.add("$id: Sender: $address\nMessage: $body")
    } while (cursor.moveToNext() && result.size < 10)

    cursor.close()
  } else {
    Log.e("ReadMessage", "Cannot initialize cursor to read SMS")
  }

  return result
}