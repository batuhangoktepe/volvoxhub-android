package com.volvoxmobile.volvoxhub.data.remote.api.hub

import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.RegisterRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.ClaimRewardResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RewardStatusResponse
import com.google.gson.JsonObject
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.MessageTicketRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.NewTicketRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.PromoCodeRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.SocialLoginRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.CreateNewMessageResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.CreateNewTicketResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.DeleteAccountResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.GetProductsResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.PromoCodeResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketsResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HubApiService {
    @POST("device/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest,
    ): RegisterBaseResponse

    @POST("device/reward")
    suspend fun claimReward(): ClaimRewardResponse

    @GET("device/reward")
    suspend fun rewardStatus(): RewardStatusResponse

    @POST("device/conversion")
    suspend fun updateConversion(@Body conversionData: JsonObject): ResponseBody

    @POST("promo-codes/use")
    suspend fun usePromoCode(@Body promoCodeRequest: PromoCodeRequest): PromoCodeResponse

    @GET("support/tickets")
    suspend fun getTickets(): SupportTicketsResponse

    @GET("support/tickets/{ticket_id}")
    suspend fun getTicket(
        @Path("ticket_id") ticketId: String
    ): SupportTicketResponse

    @POST("support/tickets")
    suspend fun createNewTicket(@Body newTicketRequest: NewTicketRequest): CreateNewTicketResponse

    @POST("support/tickets/{ticket_id}/messages")
    suspend fun createNewMessage(
        @Path("ticket_id") ticketId: String,
        @Body messageTicketRequest: MessageTicketRequest
    ): CreateNewMessageResponse

    @POST("device/social-login")
    suspend fun socialLogin(
        @Body socialLoginRequest: SocialLoginRequest
    ): Boolean

    @DELETE("device")
    suspend fun deleteAccount() : DeleteAccountResponse

    @GET("product/app")
    suspend fun getProducts(): GetProductsResponse
}
