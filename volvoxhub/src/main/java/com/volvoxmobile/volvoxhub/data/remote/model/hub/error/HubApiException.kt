package com.volvoxmobile.volvoxhub.data.remote.model.hub.error

class HubApiException(
    message: String? = null,
    private val errorResponse: HubErrorResponse? = null,
) : Throwable(message)
