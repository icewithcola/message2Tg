package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextQuote(
  @Json(name = "text")
  val text: String,

  @Json(name = "entities")
  val entities: List<MessageEntity>? = null,

  @Json(name = "position")
  val position: Long,

  @Json(name = "is_manual")
  val isManual: Boolean? = null,
)
