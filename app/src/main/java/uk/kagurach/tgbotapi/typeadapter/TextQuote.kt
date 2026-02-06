package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextQuote(
  @field:Json(name = "text")
  val text: String,

  @field:Json(name = "entities")
  val entities: List<MessageEntity>? = null,

  @field:Json(name = "position")
  val position: Long,

  @field:Json(name = "is_manual")
  val isManual: Boolean? = null,
)