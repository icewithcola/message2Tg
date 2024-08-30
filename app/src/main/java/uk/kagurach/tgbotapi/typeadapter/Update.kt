package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Update(
  @Json(name = "update_id")
  val updateId: Long,

  @Json(name = "message")
  val message: Message? = null,

  @Json(name = "edited_message")
  val editedMessage: Message? = null,

  @Json(name = "channel_post")
  val channelPost: Message? = null,

  @Json(name = "edited_channel_post")
  val editedChannelPost: Message? = null,

  // @Json(name = "business_connection")
  // val businessConnection: BusinessConnection? = null,

  @Json(name = "business_message")
  val businessMessage: Message? = null,

  @Json(name = "edited_business_message")
  val editedBusinessMessage: Message? = null,

  // @Json(name = "deleted_business_messages")
  // val deletedBusinessMessages: BusinessMessagesDeleted? = null,
  //
  // @Json(name = "message_reaction")
  // val messageReaction: MessageReactionUpdated? = null,
  //
  // @Json(name = "message_reaction_count")
  // val messageReactionCount: MessageReactionCountUpdated? = null,
  //
  // @Json(name = "inline_query")
  // val inlineQuery: InlineQuery? = null,
  //
  // @Json(name = "chosen_inline_result")
  // val chosenInlineResult: ChosenInlineResult? = null,
  //
  // @Json(name = "callback_query")
  // val callbackQuery: CallbackQuery? = null,
  //
  // @Json(name = "shipping_query")
  // val shippingQuery: ShippingQuery? = null,
  //
  // @Json(name = "pre_checkout_query")
  // val preCheckoutQuery: PreCheckoutQuery? = null,
  //
  // @Json(name = "poll")
  // val poll: Poll? = null,
  //
  // @Json(name = "poll_answer")
  // val pollAnswer: PollAnswer? = null,
  //
  // @Json(name = "my_chat_member")
  // val myChatMember: ChatMemberUpdated? = null,
  //
  // @Json(name = "chat_member")
  // val chatMember: ChatMemberUpdated? = null,
  //
  // @Json(name = "chat_join_request")
  // val chatJoinRequest: ChatJoinRequest? = null,
  //
  // @Json(name = "chat_boost")
  // val chatBoost: ChatBoostUpdated? = null,
  //
  // @Json(name = "removed_chat_boost")
  // val removedChatBoost: ChatBoostRemoved? = null
)
