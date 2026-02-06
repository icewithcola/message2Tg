package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Chat(
  @field:Json(name = "id")
  val id: Long,

  @field:Json(name = "type")
  val type: String,

  @field:Json(name = "title")
  val title: String? = null,

  @field:Json(name = "username")
  val username: String? = null,

  @field:Json(name = "first_name")
  val firstName: String? = null,

  @field:Json(name = "last_name")
  val lastName: String? = null,

  @field:Json(name = "is_forum")
  val isForum: Boolean? = null // only null and true is valid
)