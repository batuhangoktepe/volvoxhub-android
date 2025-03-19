package com.volvoxmobile.volvoxhub.domain.remote.hub

import com.google.gson.JsonObject
import com.volvoxmobile.volvoxhub.common.util.GenericResult
import com.volvoxmobile.volvoxhub.common.util.handleHubRequest
import com.volvoxmobile.volvoxhub.data.remote.api.hub.HubApiService
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.MessageTicketRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.NewTicketRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.PromoCodeRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.QrLoginRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.RegisterRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.SocialLoginRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.ClaimRewardResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.CreateNewMessageResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.CreateNewTicketResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.DeleteAccountResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.GetProductsResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.PromoCodeResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.QrLoginResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RewardStatusResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketsResponse
import okhttp3.ResponseBody

class HubApiRepositoryImpl(
    private val hubApiService: HubApiService,
) : HubApiRepository {
    override suspend fun register(registerRequest: RegisterRequest): GenericResult<RegisterBaseResponse> =
        handleHubRequest { hubApiService.register(registerRequest) }

    override suspend fun claimReward(): GenericResult<ClaimRewardResponse> =
        handleHubRequest { hubApiService.claimReward() }

    override suspend fun rewardStatus(): GenericResult<RewardStatusResponse> =
        handleHubRequest { hubApiService.rewardStatus() }

    override suspend fun updateConversion(conversionData: JsonObject): GenericResult<ResponseBody> =
        handleHubRequest { hubApiService.updateConversion(conversionData) }

    override suspend fun usePromoCode(promoCodeRequest: PromoCodeRequest): GenericResult<PromoCodeResponse> =
        handleHubRequest { hubApiService.usePromoCode(promoCodeRequest) }

    override suspend fun getTickets(): GenericResult<SupportTicketsResponse> =
        handleHubRequest { hubApiService.getTickets() }

    override suspend fun getTicket(ticketId: String): GenericResult<SupportTicketResponse> =
        handleHubRequest { hubApiService.getTicket(ticketId) }

    override suspend fun createNewTicket(newTicketRequest: NewTicketRequest): GenericResult<CreateNewTicketResponse> =
        handleHubRequest { hubApiService.createNewTicket(newTicketRequest) }

    override suspend fun createNewMessage(
        ticketId: String,
        messageTicketRequest: MessageTicketRequest
    ): GenericResult<CreateNewMessageResponse> =
        handleHubRequest { hubApiService.createNewMessage(ticketId, messageTicketRequest) }

    override suspend fun socialLogin(socialLoginRequest: SocialLoginRequest): GenericResult<Boolean> =
        handleHubRequest { hubApiService.socialLogin(socialLoginRequest) }

    override suspend fun deleteAccount(): GenericResult<DeleteAccountResponse> =
        handleHubRequest { hubApiService.deleteAccount() }

    override suspend fun getProducts(): GenericResult<GetProductsResponse> =
        handleHubRequest { hubApiService.getProducts() }

    override suspend fun approveQrLogin(qrLoginRequest: QrLoginRequest): GenericResult<QrLoginResponse> =
        handleHubRequest { hubApiService.approveQrLogin(qrLoginRequest) }
}
