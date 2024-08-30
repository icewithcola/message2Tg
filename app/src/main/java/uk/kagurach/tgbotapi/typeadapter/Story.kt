package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Story(
  @Json(name = "chat")
  val chat: Chat,

  @Json(name = "id")
  val id: Long,
)
