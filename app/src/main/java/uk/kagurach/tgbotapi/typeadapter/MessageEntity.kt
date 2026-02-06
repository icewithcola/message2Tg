package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageEntity(
  @field:Json(name = "type")
  val type: String,

  @field:Json(name = "offset")
  val offset: Long,

  @field:Json(name = "length")
  val length: Long,

  @field:Json(name = "url")
  val url: String? = null,

  @field:Json(name = "user")
  val user: User? = null,

  @field:Json(name = "language")
  val language: String? = null,

  @field:Json(name = "custom_emoji_id")
  val customEmojiId: String? = null
)