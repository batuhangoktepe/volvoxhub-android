package com.volvoxmobile.volvoxhub.domain.remote.hub

import com.volvoxmobile.volvoxhub.common.util.GenericResult
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.RegisterRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.ClaimRewardResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RewardStatusResponse
import okhttp3.ResponseBody
import com.google.gson.JsonObject
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.PromoCodeRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.PromoCodeResponse

interface HubApiRepository {
    suspend fun register(registerRequest: RegisterRequest): GenericResult<RegisterBaseResponse>
    suspend fun claimReward(): GenericResult<ClaimRewardResponse>
    suspend fun rewardStatus(): GenericResult<RewardStatusResponse>
    suspend fun updateConversion(conversionData: JsonObject): GenericResult<ResponseBody>
    suspend fun usePromoCode(promoCodeRequest: PromoCodeRequest): GenericResult<PromoCodeResponse>
}
