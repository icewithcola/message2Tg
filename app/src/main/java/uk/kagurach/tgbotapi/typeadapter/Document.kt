package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Document(
  @field:Json(name = "file_id")
  val fileId: String,

  @field:Json(name = "file_unique_id")
  val fileUniqueId: String,

  @field:Json(name = "thumbnail")
  val thumbnail: PhotoSize? = null,

  @field:Json(name = "file_name")
  val fileName: String? = null,

  @field:Json(name = "mime_type")
  val mimeType: String? = null,

  @field:Json(name = "file_size")
  val fileSize: Long? = null,

  )