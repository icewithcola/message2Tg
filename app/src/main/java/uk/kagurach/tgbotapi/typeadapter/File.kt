package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class File(
  @Json(name = "file_id")
  val fileId: String,

  @Json(name = "file_unique_id")
  val fileUniqueId: String,

  @Json(name = "file_size")
  val fileSize: Long? = null,

  @Json(name = "file_path")
  val filePath: String? = null,
)
