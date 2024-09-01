package uk.kagurach.tgbotapi

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uk.kagurach.tgbotapi.typeadapter.MessageReturned
import uk.kagurach.tgbotapi.typeadapter.UpdatesReturned
import uk.kagurach.tgbotapi.typeadapter.UserReturned

interface BotApiInterface {

  @GET("/bot{token}/getMe")
  suspend fun getMe(
    @Path("token") token: String
  ): UserReturned

  @GET("/bot{token}/sendMessage")
  suspend fun sendMessage(
    @Path("token") token: String,
    @Query("chat_id") chatId: Long,
    @Query("text") text: String,
    @Query("disable_notification") disableNotification: Boolean?
  ): MessageReturned

  @GET("/bot{token}/getUpdates")
  suspend fun getUpdates(
    @Path("token") token: String,
    @Query("offset") offset: Int?,
    @Query("limit") limit: Int?,
    @Query("timeout") timeout: Int?
  ): UpdatesReturned
}