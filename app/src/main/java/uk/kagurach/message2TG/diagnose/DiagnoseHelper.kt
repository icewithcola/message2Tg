package uk.kagurach.message2TG.diagnose

import android.content.Context
import uk.kagurach.message2TG.gen.Version
import uk.kagurach.message2TG.util.SystemHelper

/**
 * Helper object for diagnosing functionalities.
 */
object DiagnoseHelper {
  fun diagnose(context: Context): List<Pair<String, Boolean>> =
    mutableListOf<Pair<String, Boolean>>().apply {
      add("readMessage" to readMessage(context))
      add("getBatteryLevel" to getBatteryLevel(context))
    }


  fun buildInfo(): List<Pair<String, String>> =
    mutableListOf<Pair<String, String>>().apply {
      add("Build time" to Version.BUILD_TIME)
      add("Commit hash" to Version.GIT_COMMIT_ID)
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