package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface Returned<out T> {
  val ok: Boolean
  val result: T
}

@JsonClass(generateAdapter = true)
class UserReturned(
  @field:Json(name = "ok")
  override val ok: Boolean,

  @field:Json(name = "result")
  override val result: User
): Returned<User>

@JsonClass(generateAdapter = true)
class MessageReturned(
  @field:Json(name = "ok")
  override val ok: Boolean,

  @field:Json(name = "result")
  override val result: Message
): Returned<Message>


@JsonClass(generateAdapter = true)
class UpdatesReturned(
  @field:Json(name = "ok")
  override val ok: Boolean,

  @field:Json(name = "result")
  override val result: List<Update>
): Returned<List<Update>>