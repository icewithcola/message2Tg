package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoSize(
  @field:Json(name = "file_id")
  val fileId: String,

  @field:Json(name = "file_unique_id")
  val fileUniqueId: String,

  @field:Json(name = "width")
  val width: Long,

  @field:Json(name = "height")
  val height: Long,

  @field:Json(name = "file_size")
  val fileSize: Long? = null
)