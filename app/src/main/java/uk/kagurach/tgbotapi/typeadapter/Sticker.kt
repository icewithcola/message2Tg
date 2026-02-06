package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Sticker(
  @field:Json(name = "file_id")
  val fileId: String,

  @field:Json(name = "file_unique_id")
  val fileUniqueId: String,

  @field:Json(name = "type")
  val type: String,

  @field:Json(name = "width")
  val width: Long,

  @field:Json(name = "height")
  val height: Long,

  @field:Json(name = "is_animated")
  val isAnimated: Boolean,

  @field:Json(name = "is_video")
  val isVideo: Boolean,

  @field:Json(name = "thumbnail")
  val thumbnail: PhotoSize? = null,

  @field:Json(name = "emoji")
  val emoji: String? = null,

  @field:Json(name = "set_name")
  val setName: String? = null,

  @field:Json(name = "premium_animation")
  val premiumAnimation: File? = null,

  // @Json(name = "mask_position")
  // val maskPosition: MaskPosition? = null,

  @field:Json(name = "custom_emoji_id")
  val customEmojiId: String? = null,

  @field:Json(name = "needs_repainting")
  val needsRepainting: Boolean? = null,

  @field:Json(name = "file_size")
  val fileSize: Long? = null,
)