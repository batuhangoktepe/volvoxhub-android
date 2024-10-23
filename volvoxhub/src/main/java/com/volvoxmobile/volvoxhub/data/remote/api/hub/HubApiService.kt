package com.volvoxmobile.volvoxhub.data.remote.api.hub

import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.RegisterRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface HubApiService {
    // Auth Services
    @POST("device/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest,
    ): RegisterBaseResponse
}
