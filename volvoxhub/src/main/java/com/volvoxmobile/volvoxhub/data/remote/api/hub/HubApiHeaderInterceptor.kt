package com.volvoxmobile.volvoxhub.data.remote.api.hub

import android.content.Context
import com.volvoxmobile.volvoxhub.common.util.DeviceUuidFactory
import okhttp3.Interceptor
import okhttp3.Response

class HubApiHeaderInterceptor(
    private val context: Context,
    private val appId: String,
    private val appName: String,
    private val vIdProvider: () -> String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        /*
        requestBuilder
            .addHeader(HEADER_HUB_ID, appId)
            .addHeader(HEADER_HUB_DEVICE_ID, DeviceUuidFactory.create(context = context, appName = appName))

        val currentVId = vIdProvider()
        if (currentVId.isNotEmpty()) {
            requestBuilder.addHeader(HEADER_VID, currentVId)
        }
        */

        requestBuilder
            .addHeader(HEADER_HUB_ID, "fec9e0c3-b781-4fa7-b04e-cc568d906e78")
            .addHeader(HEADER_HUB_DEVICE_ID,"A4F33019E140499C949B0C8E1DB884E7")
            .addHeader(HEADER_VID, "72e5a62c-29ef-4a24-af8f-0a9d6f0b2c40")

        val modifiedRequest = requestBuilder.build()
        return chain.proceed(modifiedRequest)
    }

    companion object {
        private const val HEADER_HUB_ID = "X-Hub-Id"
        private const val HEADER_HUB_DEVICE_ID = "X-Hub-Device-Id"
        private const val HEADER_VID = "X-Hub-Vid"
    }
}
