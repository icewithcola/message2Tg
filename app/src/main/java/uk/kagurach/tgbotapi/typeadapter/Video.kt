package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Video(
  @Json(name = "file_id")
  val fileId: String,

  @Json(name = "file_unique_id")
  val fileUniqueId: String,

  @Json(name = "width")
  val width: Long,

  @Json(name = "height")
  val height: Long,

  @Json(name = "duration")
  val duration: Long,

  @Json(name = "thumbnail")
  val thumbnail: PhotoSize? = null,

  @Json(name = "file_name")
  val fileName: String? = null,

  @Json(name = "mime_type")
  val mimeType: String? = null,

  @Json(name = "file_size")
  val fileSize: Long? = null,
)
