package uk.kagurach.message2TG.diagnose

import android.content.Context
import uk.kagurach.message2TG.util.SystemHelper

/**
 * Helper object for diagnosing functionalities.
 */
object DiagnoseHelper {
  fun diagnose(context: Context): List<Pair<String, Boolean>>{
    var pair = mutableListOf<Pair<String, Boolean>>()
    pair.add("readMessage" to readMessage(context))
    pair.add("getBatteryLevel" to getBatteryLevel(context))
    return pair
  }

  private fun readMessage(context: Context): Boolean {
    try {
      val messages = SystemHelper.readMessage(context)
      return messages.isNotEmpty()
    }catch (_: Exception){
      return false
    }
  }

  private fun getBatteryLevel(context: Context): Boolean {
    try {
      SystemHelper.getBatteryLevel(context)
    }catch (_: Exception){
      return false
    }
    return true
  }
}