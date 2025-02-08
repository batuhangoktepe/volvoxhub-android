package com.volvoxmobile.volvoxhub.domain.remote.hub

import com.volvoxmobile.volvoxhub.common.util.GenericResult
import com.volvoxmobile.volvoxhub.common.util.handleHubRequest
import com.volvoxmobile.volvoxhub.data.remote.api.hub.HubApiService
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.RegisterRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.ClaimRewardResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RewardStatusResponse
import okhttp3.ResponseBody
import com.google.gson.JsonObject

class HubApiRepositoryImpl(
    private val hubApiService: HubApiService,
) : HubApiRepository {
    override suspend fun register(registerRequest: RegisterRequest): GenericResult<RegisterBaseResponse> =
        handleHubRequest { hubApiService.register(registerRequest) }

    override suspend fun claimReward(): GenericResult<ClaimRewardResponse> = handleHubRequest { hubApiService.claimReward() }

    override suspend fun rewardStatus(): GenericResult<RewardStatusResponse> = handleHubRequest { hubApiService.rewardStatus() }

    override suspend fun updateConversion(conversionData: JsonObject): GenericResult<ResponseBody> = 
        handleHubRequest { hubApiService.updateConversion(conversionData) }
}
