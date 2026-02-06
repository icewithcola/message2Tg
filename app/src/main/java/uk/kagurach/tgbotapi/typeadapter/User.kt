package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
  @field:Json(name = "id")
  val id: Long,

  @field:Json(name = "is_bot")
  val isBot: Boolean,

  @field:Json(name = "first_name")
  val firstName: String,

  @field:Json(name = "last_name")
  val lastName: String? = null,

  @field:Json(name = "username")
  val username: String? = null,

  @field:Json(name = "language_code")
  val languageCode: String? = null,

  @field:Json(name = "is_premium")
  val isPremium: Boolean? = null,

  @field:Json(name = "added_to_attachment_menu")
  val addedToAttachmentMenu: Boolean? = null,

  @field:Json(name = "can_join_groups")
  val canJoinGroups: Boolean? = null,

  @field:Json(name = "can_read_all_group_messages")
  val canReadAllGroupMessages: Boolean? = null,

  @field:Json(name = "supports_inline_queries")
  val supportsInlineQueries: Boolean? = null,

  @field:Json(name = "can_connect_to_business")
  val canConnectToBusiness: Boolean? = null,

  @field:Json(name = "has_main_web_app")
  val hasMainWebApp: Boolean? = null
)
