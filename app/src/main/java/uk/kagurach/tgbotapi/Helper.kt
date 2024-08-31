package uk.kagurach.tgbotapi

/** Validate if all args are not null or
 *  default value has initialized
 */
fun validateArgNotNullOrHasDefault(isInitialized: Boolean, vararg args: Any?): Boolean {
  if (isInitialized) {
    return true
  }
  args.forEach {
    if (it == null) {
      return false
    }
  }
  return true
}

fun validateBotToken(token: String): Boolean =
  token.matches("^[0-9]{8,10}:[a-zA-Z0-9_-]{35}\$".toRegex())

fun validatePartialBotToken(token: String): Boolean {
  var metColon = false
  token.forEach { c ->
    if (!metColon) {
      if (c == ':') {
        metColon = true
      } else if (c !in '0'..'9') {
        return false
      }
    } else if (c !in '0'..'9' && c !in 'a'..'z' && c !in 'A'..'Z' && c != '_' && c != '-') {
      return false
    }
  }
  return true
}