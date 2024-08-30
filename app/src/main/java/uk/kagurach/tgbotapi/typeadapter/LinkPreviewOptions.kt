package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinkPreviewOptions(
  @Json(name = "is_disabled")
  val isDisabled: Boolean? = null,

  @Json(name = "url")
  val url: String? = null,

  @Json(name = "prefer_small_media")
  val preferSmallMedia: Boolean? = null,

  @Json(name = "prefer_large_media")
  val preferLargeMedia: Boolean? = null,

  @Json(name = "show_above_text")
  val showAboveText: Boolean? = null
)
