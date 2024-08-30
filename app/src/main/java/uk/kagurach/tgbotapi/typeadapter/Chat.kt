package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Chat(
  @Json(name = "id")
  val id: Long,

  @Json(name = "type")
  val type: String,

  @Json(name = "title")
  val title: String? = null,

  @Json(name = "username")
  val username: String? = null,

  @Json(name = "first_name")
  val firstName: String? = null,

  @Json(name = "last_name")
  val lastName: String? = null,

  @Json(name = "is_forum")
  val isForum: Boolean? = null // only null and true is valid
)
