package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface Returned<out T> {
  val ok: Boolean
  val result: T
}

@JsonClass(generateAdapter = true)
class UserReturned(
  @Json(name = "ok")
  override val ok: Boolean,

  @Json(name = "result")
  override val result: User
): Returned<User>

@JsonClass(generateAdapter = true)
class MessageReturned(
  @Json(name = "ok")
  override val ok: Boolean,

  @Json(name = "result")
  override val result: Message
): Returned<Message>


@JsonClass(generateAdapter = true)
class UpdatesReturned(
  @Json(name = "ok")
  override val ok: Boolean,

  @Json(name = "result")
  override val result: List<Update>
): Returned<List<Update>>
