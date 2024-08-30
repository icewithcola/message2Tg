package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Document(
  @Json(name = "file_id")
  val fileId: String,

  @Json(name = "file_unique_id")
  val fileUniqueId: String,

  @Json(name = "thumbnail")
  val thumbnail: PhotoSize? = null,

  @Json(name = "file_name")
  val fileName: String? = null,

  @Json(name = "mime_type")
  val mimeType: String? = null,

  @Json(name = "file_size")
  val fileSize: Long? = null,

  )
