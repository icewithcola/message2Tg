package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfilePhotos(
  @field:Json(name = "total_count")
  val totalCount: Long,

  @field:Json(name = "photos")
  val photos: List<List<PhotoSize>>
)