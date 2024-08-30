package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageOrigin(
  @Json(name = "type")
  val type: String,

  @Json(name = "date")
  val date: Long,

  @Json(name = "sender_user")
  val senderUser: User? = null,

  @Json(name = "sender_user_name")
  val senderUserName: String? = null,

  @Json(name = "sender_chat")
  val senderChat: Chat? = null,

  @Json(name = "author_signature")
  val authorSignature: String? = null,

  @Json(name = "chat")
  val chat: Chat? = null,

  @Json(name = "message_id")
  val messageId: Long? = null

)