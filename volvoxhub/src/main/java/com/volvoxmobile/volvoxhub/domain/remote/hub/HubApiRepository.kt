package com.volvoxmobile.volvoxhub.domain.remote.hub

import com.volvoxmobile.volvoxhub.common.util.GenericResult
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.RegisterRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.ClaimRewardResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RewardStatusResponse
import okhttp3.ResponseBody
import com.google.gson.JsonObject
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.MessageTicketRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.NewTicketRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.PromoCodeRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.CreateNewMessageResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.CreateNewTicketResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.PromoCodeResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketsResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.UnseenStatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HubApiRepository {
    suspend fun register(registerRequest: RegisterRequest): GenericResult<RegisterBaseResponse>
    suspend fun claimReward(): GenericResult<ClaimRewardResponse>
    suspend fun rewardStatus(): GenericResult<RewardStatusResponse>
    suspend fun updateConversion(conversionData: JsonObject): GenericResult<ResponseBody>
    suspend fun usePromoCode(promoCodeRequest: PromoCodeRequest): GenericResult<PromoCodeResponse>
    suspend fun getTickets(): GenericResult<SupportTicketsResponse>
    suspend fun getTicket(
        ticketId: String
    ): GenericResult<SupportTicketResponse>
    suspend fun createNewTicket(newTicketRequest: NewTicketRequest): GenericResult<CreateNewTicketResponse>
    suspend fun createNewMessage(
        ticketId: String,
        messageTicketRequest: MessageTicketRequest
    ): GenericResult<CreateNewMessageResponse>
    suspend fun getUnseenStatus(): GenericResult<UnseenStatusResponse>
}
