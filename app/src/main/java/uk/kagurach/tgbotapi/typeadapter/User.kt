package uk.kagurach.tgbotapi.typeadapter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
  @Json(name = "id")
  val id: Long,

  @Json(name = "is_bot")
  val isBot: Boolean,

  @Json(name = "first_name")
  val firstName: String,

  @Json(name = "last_name")
  val lastName: String? = null,

  @Json(name = "username")
  val username: String? = null,

  @Json(name = "language_code")
  val languageCode: String? = null,

  @Json(name = "is_premium")
  val isPremium: Boolean? = null,

  @Json(name = "added_to_attachment_menu")
  val addedToAttachmentMenu: Boolean? = null,

  @Json(name = "can_join_groups")
  val canJoinGroups: Boolean? = null,

  @Json(name = "can_read_all_group_messages")
  val canReadAllGroupMessages: Boolean? = null,

  @Json(name = "supports_inline_queries")
  val supportsInlineQueries: Boolean? = null,

  @Json(name = "can_connect_to_business")
  val canConnectToBusiness: Boolean? = null,

  @Json(name = "has_main_web_app")
  val hasMainWebApp: Boolean? = null
)