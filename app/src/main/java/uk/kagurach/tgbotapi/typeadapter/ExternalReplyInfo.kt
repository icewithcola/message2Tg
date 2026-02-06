package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExternalReplyInfo(
  @field:Json(name = "origin")
  val origin: MessageOrigin,

  @field:Json(name = "chat")
  val chat: Chat? = null,

  @field:Json(name = "message_id")
  val messageId: Long? = null,

  @field:Json(name = "link_preview_options")
  val linkPreviewOptions: LinkPreviewOptions? = null,

  @field:Json(name = "animation")
  val animation: Animation? = null,

  @field:Json(name = "audio")
  val audio: Audio? = null,

  @field:Json(name = "document")
  val document: Document? = null,

  // @Json(name = "paid_media")
  // val paidMedia: PaidMediaInfo? = null,

  @field:Json(name = "photo")
  val photo: List<PhotoSize>? = null,

  @field:Json(name = "sticker")
  val sticker: Sticker? = null,

  @field:Json(name = "story")
  val story: Story? = null,

  @field:Json(name = "video")
  val video: Video? = null,

  @field:Json(name = "video_note")
  val videoNote: VideoNote? = null,

  @field:Json(name = "voice")
  val voice: Voice? = null,

  @field:Json(name = "has_media_spoiler")
  val hasMediaSpoiler: Boolean? = null,

  // @Json(name = "contact")
  // val contact: Contact? = null,
  //
  // @Json(name = "dice")
  // val dice: Dice? = null,
  //
  // @Json(name = "game")
  // val game: Game? = null,
  //
  // @Json(name = "giveaway")
  // val giveaway: Giveaway? = null,
  //
  // @Json(name = "giveaway_winners")
  // val giveawayWinners: GiveawayWinners? = null,
  //
  // @Json(name = "invoice")
  // val invoice: Invoice? = null,
  //
  // @Json(name = "location")
  // val location: Location? = null,
  //
  // @Json(name = "poll")
  // val poll: Poll? = null,
  //
  // @Json(name = "venue")
  // val venue: Venue? = null,
)