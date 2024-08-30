package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoSize(
  @Json(name = "file_id")
  val fileId: String,

  @Json(name = "file_unique_id")
  val fileUniqueId: String,

  @Json(name = "width")
  val width: Long,

  @Json(name = "height")
  val height: Long,

  @Json(name = "file_size")
  val fileSize: Long? = null
)
