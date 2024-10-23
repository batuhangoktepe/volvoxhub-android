package com.volvoxmobile.volvoxhub.domain.remote.hub

import com.volvoxmobile.volvoxhub.common.util.GenericResult
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.RegisterRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse

interface HubApiRepository {
    suspend fun register(registerRequest: RegisterRequest): GenericResult<RegisterBaseResponse>
}
