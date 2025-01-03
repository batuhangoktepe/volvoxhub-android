package com.volvoxmobile.volvoxhub.data.remote.api.hub

import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.RegisterRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.ClaimRewardResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RewardStatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HubApiService {
    @POST("device/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest,
    ): RegisterBaseResponse

    @POST("device/reward")
    suspend fun claimReward(): ClaimRewardResponse

    @GET("device/reward")
    suspend fun rewardStatus(): RewardStatusResponse
}
