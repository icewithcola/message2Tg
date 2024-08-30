package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserReturned(
  @Json(name = "ok")
  val ok: Boolean,

  @Json(name = "result")
  val result: User
)

@JsonClass(generateAdapter = true)
class MessageReturned(
  @Json(name = "ok")
  val ok: Boolean,

  @Json(name = "result")
  val result: Message
)

@JsonClass(generateAdapter = true)
class UpdatesReturned(
  @Json(name = "ok")
  val ok: Boolean,

  @Json(name = "result")
  val result: List<Update>
)