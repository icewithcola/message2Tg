package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageEntity(
  @Json(name = "type")
  val type: String,

  @Json(name = "offset")
  val offset: Long,

  @Json(name = "length")
  val length: Long,

  @Json(name = "url")
  val url: String? = null,

  @Json(name = "user")
  val user: User? = null,

  @Json(name = "language")
  val language: String? = null,

  @Json(name = "custom_emoji_id")
  val customEmojiId: String? = null
)
