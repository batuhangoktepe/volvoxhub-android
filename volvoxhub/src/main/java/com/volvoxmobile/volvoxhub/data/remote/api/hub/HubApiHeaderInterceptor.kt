package com.volvoxmobile.volvoxhub.data.remote.api.hub

import android.content.Context
import com.volvoxmobile.volvoxhub.common.util.DeviceUuidFactory
import okhttp3.Interceptor
import okhttp3.Response

class HubApiHeaderInterceptor(
    private val context: Context,
    private val appId: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        val modifiedRequest =
            requestBuilder
                .addHeader(HEADER_HUB_ID, appId)
                .addHeader(HEADER_HUB_DEVICE_ID, DeviceUuidFactory.create(context))
                .build()

        return chain.proceed(modifiedRequest)
    }

    companion object {
        private const val HEADER_HUB_ID = "X-Hub-Id"
        private const val HEADER_HUB_DEVICE_ID = "X-Hub-Device-Id"
    }
}
