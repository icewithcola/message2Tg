package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LinkPreviewOptions(
  @field:Json(name = "is_disabled")
  val isDisabled: Boolean? = null,

  @field:Json(name = "url")
  val url: String? = null,

  @field:Json(name = "prefer_small_media")
  val preferSmallMedia: Boolean? = null,

  @field:Json(name = "prefer_large_media")
  val preferLargeMedia: Boolean? = null,

  @field:Json(name = "show_above_text")
  val showAboveText: Boolean? = null
)