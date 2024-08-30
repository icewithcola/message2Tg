package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfilePhotos(
  @Json(name = "total_count")
  val totalCount: Long,

  @Json(name = "photos")
  val photos: List<List<PhotoSize>>
)
