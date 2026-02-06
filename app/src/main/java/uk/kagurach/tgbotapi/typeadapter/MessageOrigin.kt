package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageOrigin(
  @field:Json(name = "type")
  val type: String,

  @field:Json(name = "date")
  val date: Long,

  @field:Json(name = "sender_user")
  val senderUser: User? = null,

  @field:Json(name = "sender_user_name")
  val senderUserName: String? = null,

  @field:Json(name = "sender_chat")
  val senderChat: Chat? = null,

  @field:Json(name = "author_signature")
  val authorSignature: String? = null,

  @field:Json(name = "chat")
  val chat: Chat? = null,

  @field:Json(name = "message_id")
  val messageId: Long? = null

)