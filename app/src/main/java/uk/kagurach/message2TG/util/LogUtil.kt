package uk.kagurach.message2TG.util

import android.util.Log

class LogUtil private constructor(){
  companion object{
    val instance by lazy{ LogUtil() }
  }

}

fun loge(tag:String,msg: String) = Log.e("message2TG","$tag throws this:\n$msg")
fun logi(tag:String,msg: String) = Log.i("message2TG","$tag informs this:\n$msg")