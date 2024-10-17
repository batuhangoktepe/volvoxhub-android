package com.volvoxmobile.volvoxhub.common.amplitude

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.android.events.Identify
import com.volvoxmobile.volvoxhub.common.util.DeviceUuidFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object AmplitudeManager {

    private lateinit var amplitude: Amplitude

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    internal fun initialize(context: Context, apiKey: String) {
        amplitude = Amplitude(
            Configuration(
                apiKey = apiKey,
                context = context
            )
        )
        val identify = Identify()
        identify.set("user-platform", "android")
        amplitude.identify(identify)
        amplitude.setUserId(DeviceUuidFactory.create(context))
    }

    fun logEvent(
        eventName: String,
        eventProperties: Map<String, Any> = emptyMap()
    ) {
        scope.launch {
            amplitude.track(eventName, eventProperties)
        }
    }
}