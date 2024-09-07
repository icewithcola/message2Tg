package uk.kagurach.message2TG.util

val verifyCodeKeyWords = listOf<String>(
  "验证码","注册码","校验码","动态码","动态密码",
  "Verify","Code","OTP","Password"
)

val numberOnlyCode = "\\d{4,6}".toRegex()
val byPunctuation = "：([0-9a-zA-Z-_]*)，".toRegex()

fun isVerifyCode(text: String): Boolean{
  for (verifyCodeKeyWord in verifyCodeKeyWords) {
    if (text.contains(verifyCodeKeyWord,true)){
      return true
    }
  }
  return false
}

fun extractVerifyCode(text: String): String?{
  if (!isVerifyCode(text)){
    return null
  }

  val numberMatch = numberOnlyCode.find(text)?.value
  val punctuationMatch = byPunctuation.find(text)?.groupValues?.get(1)

  if (punctuationMatch!=null){
    return punctuationMatch
  }
  if (numberMatch!=null){
    return numberMatch
  }

  return null
}