package uk.kagurach.message2TG.util

object CommandProcessor {
  inline fun process(command: String, expect: String, process: (String) -> Unit) {
    if (command.isEmpty() || !command.startsWith("/")) {
      return
    }

    if (command.drop(1).contentEquals(expect)) {
      process.invoke(command)
    }
  }
}