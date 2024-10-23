package com.volvoxmobile.volvoxhub.domain.remote.hub

import com.volvoxmobile.volvoxhub.common.util.GenericResult
import com.volvoxmobile.volvoxhub.common.util.handleHubRequest
import com.volvoxmobile.volvoxhub.data.remote.api.hub.HubApiService
import com.volvoxmobile.volvoxhub.data.remote.model.hub.request.RegisterRequest
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.RegisterBaseResponse

class HubApiRepositoryImpl(
    private val hubApiService: HubApiService,
) : HubApiRepository {
    override suspend fun register(registerRequest: RegisterRequest): GenericResult<RegisterBaseResponse> =
        handleHubRequest { hubApiService.register(registerRequest) }
}
