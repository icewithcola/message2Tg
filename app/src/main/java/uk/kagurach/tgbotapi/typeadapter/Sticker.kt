package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Sticker(
  @Json(name = "file_id")
  val fileId: String,

  @Json(name = "file_unique_id")
  val fileUniqueId: String,

  @Json(name = "type")
  val type: String,

  @Json(name = "width")
  val width: Long,

  @Json(name = "height")
  val height: Long,

  @Json(name = "is_animated")
  val isAnimated: Boolean,

  @Json(name = "is_video")
  val isVideo: Boolean,

  @Json(name = "thumbnail")
  val thumbnail: PhotoSize? = null,

  @Json(name = "emoji")
  val emoji: String? = null,

  @Json(name = "set_name")
  val setName: String? = null,

  @Json(name = "premium_animation")
  val premiumAnimation: File? = null,

  // @Json(name = "mask_position")
  // val maskPosition: MaskPosition? = null,

  @Json(name = "custom_emoji_id")
  val customEmojiId: String? = null,

  @Json(name = "needs_repainting")
  val needsRepainting: Boolean? = null,

  @Json(name = "file_size")
  val fileSize: Long? = null,
)
