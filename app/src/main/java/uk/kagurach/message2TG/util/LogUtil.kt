package uk.kagurach.message2TG.util

import android.os.Looper
import android.util.Log

object LogUtil {
  private fun runIdle(f: () -> Unit) = Looper.getMainLooper().queue.addIdleHandler { f(); false }

  fun loge(tag: String, msg: String) = runIdle { Log.e("message2TG", "$tag throws this:\n$msg") }
  fun logi(tag: String, msg: String) = runIdle { Log.i("message2TG", "$tag informs this:\n$msg") }
}