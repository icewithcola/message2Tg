package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Audio(
  @Json(name = "file_id")
  val fileId: String,

  @Json(name = "file_unique_id")
  val fileUniqueId: String,

  @Json(name = "duration")
  val duration: Long,

  @Json(name = "performer")
  val performer: String? = null,

  @Json(name = "title")
  val title: String? = null,

  @Json(name = "file_name")
  val fileName: String? = null,

  @Json(name = "mime_type")
  val mimeType: String? = null,

  @Json(name = "file_size")
  val fileSize: Long? = null,

  @Json(name = "thumbnail")
  val thumbnail: PhotoSize? = null,
)
