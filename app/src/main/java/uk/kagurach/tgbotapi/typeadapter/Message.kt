package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
  @Json(name = "message_id")
  val messageId: Long,

  @Json(name = "message_thread_id")
  val messageThreadId: Long? = null,

  @Json(name = "from")
  val from: User? = null,

  @Json(name = "sender_chat")
  val senderChat: Chat? = null,

  @Json(name = "sender_boost_count")
  val senderBoostCount: Long? = null,

  @Json(name = "sender_business_bot")
  val senderBusinessBot: User? = null,

  @Json(name = "date")
  val date: Long,

  @Json(name = "business_connection_id")
  val businessConnectionId: String? = null,

  @Json(name = "chat")
  val chat: Chat,

  @Json(name = "forward_origin")
  val forwardOrigin: MessageOrigin? = null,

  @Json(name = "is_topic_message")
  val isTopicMessage: Boolean? = null,

  @Json(name = "is_automatic_forward")
  val isAutomaticForward: Boolean? = null,

  @Json(name = "reply_to_message")
  val replyToMessage: Message? = null,

  @Json(name = "external_reply")
  val externalReply: ExternalReplyInfo? = null,

  @Json(name = "quote")
  val quote: TextQuote? = null,

  @Json(name = "reply_to_story")
  val replyToStory: Story? = null,

  @Json(name = "via_bot")
  val viaBot: User? = null,

  @Json(name = "edit_date")
  val editDate: Long? = null,

  @Json(name = "has_protected_content")
  val hasProtectedContent: Boolean? = null,

  @Json(name = "is_from_offline")
  val isFromOffline: Boolean? = null,

  @Json(name = "media_group_id")
  val mediaGroupId: String? = null,

  @Json(name = "author_signature")
  val authorSignature: String? = null,

  @Json(name = "text")
  val text: String? = null,

  @Json(name = "entities")
  val entities: List<MessageEntity>? = null,

  @Json(name = "link_preview_options")
  val linkPreviewOptions: LinkPreviewOptions? = null,

  @Json(name = "effect_id")
  val effectId: String? = null,

  @Json(name = "animation")
  val animation: Animation? = null,

  @Json(name = "audio")
  val audio: Audio? = null,

  @Json(name = "document")
  val document: Document? = null,

  // @Json(name = "paid_media")
  // val paidMedia: PaidMediaInfo? = null,

  @Json(name = "photo")
  val photo: List<PhotoSize>? = null,

  @Json(name = "sticker")
  val sticker: Sticker? = null,

  @Json(name = "story")
  val story: Story? = null,

  @Json(name = "video")
  val video: Video? = null,

  @Json(name = "video_note")
  val videoNote: VideoNote? = null,

  @Json(name = "voice")
  val voice: Voice? = null,

  @Json(name = "caption")
  val caption: String? = null,

  @Json(name = "caption_entities")
  val captionEntities: List<MessageEntity>? = null,

  @Json(name = "show_caption_above_media")
  val showCaptionAboveMedia: Boolean? = null,

  @Json(name = "has_media_spoiler")
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
  // @Json(name = "poll")
  // val poll: Poll? = null,
  //
  // @Json(name = "venue")
  // val venue: Venue? = null,
  //
  // @Json(name = "location")
  // val location: Location? = null,

  @Json(name = "new_chat_members")
  val newChatMembers: List<User>? = null,

  @Json(name = "left_chat_member")
  val leftChatMember: User? = null,

  @Json(name = "new_chat_title")
  val newChatTitle: String? = null,

  @Json(name = "new_chat_photo")
  val newChatPhoto: List<PhotoSize>? = null,

  @Json(name = "delete_chat_photo")
  val deleteChatPhoto: Boolean? = null,

  @Json(name = "group_chat_created")
  val groupChatCreated: Boolean? = null,

  @Json(name = "supergroup_chat_created")
  val supergroupChatCreated: Boolean? = null,

  @Json(name = "channel_chat_created")
  val channelChatCreated: Boolean? = null,

  // @Json(name = "message_auto_delete_timer_changed")
  // val messageAutoDeleteTimerChanged: MessageAutoDeleteTimerChanged? = null,

  @Json(name = "migrate_to_chat_id")
  val migrateToChatId: Long? = null,

  @Json(name = "migrate_from_chat_id")
  val migrateFromChatId: Long? = null,

  // @Json(name = "pinned_message")
  // val pinnedMessage: MaybeInaccessibleMessage? = null,
  //
  // @Json(name = "invoice")
  // val invoice: Invoice? = null,
  //
  // @Json(name = "successful_payment")
  // val successfulPayment: SuccessfulPayment? = null,
  //
  // @Json(name = "refunded_payment")
  // val refundedPayment: RefundedPayment? = null,
  //
  // @Json(name = "users_shared")
  // val usersShared: UsersShared? = null,
  //
  // @Json(name = "chat_shared")
  // val chatShared: ChatShared? = null,

  @Json(name = "connected_website")
  val connectedWebsite: String? = null,

  // @Json(name = "write_access_allowed")
  // val writeAccessAllowed: WriteAccessAllowed? = null,
  //
  // @Json(name = "passport_data")
  // val passportData: PassportData? = null,
  //
  // @Json(name = "proximity_alert_triggered")
  // val proximityAlertTriggered: ProximityAlertTriggered? = null,
  //
  // @Json(name = "boost_added")
  // val boostAdded: ChatBoostAdded? = null,
  //
  // @Json(name = "chat_background_set")
  // val chatBackgroundSet: ChatBackground? = null,
  //
  // @Json(name = "forum_topic_created")
  // val forumTopicCreated: ForumTopicCreated? = null,
  //
  // @Json(name = "forum_topic_edited")
  // val forumTopicEdited: ForumTopicEdited? = null,
  //
  // @Json(name = "forum_topic_closed")
  // val forumTopicClosed: ForumTopicClosed? = null,
  //
  // @Json(name = "forum_topic_reopened")
  // val forumTopicReopened: ForumTopicReopened? = null,
  //
  // @Json(name = "general_forum_topic_hidden")
  // val generalForumTopicHidden: GeneralForumTopicHidden? = null,
  //
  // @Json(name = "general_forum_topic_unhidden")
  // val generalForumTopicUnhidden: GeneralForumTopicUnhidden? = null,
  //
  // @Json(name = "giveaway_created")
  // val giveawayCreated: GiveawayCreated? = null,
  //
  // @Json(name = "giveaway")
  // val giveaway: Giveaway? = null,
  //
  // @Json(name = "giveaway_winners")
  // val giveawayWinners: GiveawayWinners? = null,
  //
  // @Json(name = "giveaway_completed")
  // val giveawayCompleted: GiveawayCompleted? = null,
  //
  // @Json(name = "video_chat_scheduled")
  // val videoChatScheduled: VideoChatScheduled? = null,
  //
  // @Json(name = "video_chat_started")
  // val videoChatStarted: VideoChatStarted? = null,
  //
  // @Json(name = "video_chat_ended")
  // val videoChatEnded: VideoChatEnded? = null,
  //
  // @Json(name = "video_chat_participants_invited")
  // val videoChatParticipantsInvited: VideoChatParticipantsInvited? = null,
  //
  // @Json(name = "web_app_data")
  // val webAppData: WebAppData? = null,
  //
  // @Json(name = "reply_markup")
  // val replyMarkup: InlineKeyboardMarkup? = null,
)
