package com.volvoxmobile.volvoxhub.common.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.volvoxmobile.volvoxhub.common.util.StringUtils
import com.volvoxmobile.volvoxhub.data.remote.model.hub.error.HubApiException
import com.volvoxmobile.volvoxhub.data.remote.model.hub.error.HubErrorResponse
import retrofit2.HttpException
import java.lang.Exception

fun Throwable.asHubApiException(): HubApiException =
    if (this is HttpException) {
        try {
            val gson = Gson()
            val type = object : TypeToken<HubErrorResponse>() {}.type
            val errorResponse: HubErrorResponse = gson.fromJson(response()?.errorBody()?.charStream(), type)
            HubApiException(message = errorResponse.message, errorResponse = errorResponse)
        } catch (e: Exception) {
            val errorResponse = HubErrorResponse(statusCode = this.code(), message = StringUtils.EMPTY)
            HubApiException(errorResponse = errorResponse)
        }
    } else {
        HubApiException()
    }
