package uk.kagurach.tgbotapi

/** Validate if all args are not null or
 *  default value has initialized
 */
fun validate(isInitialized: Boolean, vararg args: Any?): Boolean {
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