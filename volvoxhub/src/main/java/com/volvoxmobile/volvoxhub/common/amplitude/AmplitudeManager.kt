package com.volvoxmobile.volvoxhub.common.amplitude

import android.app.Application
import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.android.events.Identify
import com.amplitude.experiment.Experiment
import com.amplitude.experiment.ExperimentClient
import com.amplitude.experiment.ExperimentConfig
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.volvoxmobile.volvoxhub.common.util.DeviceUuidFactory
import com.volvoxmobile.volvoxhub.common.util.StringUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object AmplitudeManager {
    private lateinit var amplitude: Amplitude
    private lateinit var experimentClient: ExperimentClient
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    internal fun initialize(
        context: Context,
        apiKey: String,
        appName: String,
        experimentKey: String = StringUtils.EMPTY
    ) {
        amplitude = Amplitude(
            Configuration(
                apiKey = apiKey,
                context = context,
            )
        )
        val identify = Identify()
        identify.set("user-platform", "android")
        amplitude.identify(identify)
        amplitude.setUserId(DeviceUuidFactory.create(context = context, appName = appName))

        if (experimentKey.isEmpty()) return
        val application = getApplication() ?: return
        experimentClient = Experiment.initializeWithAmplitudeAnalytics(
            application,
            experimentKey,
            ExperimentConfig()
        )
        setExperiment()
    }

    private fun getApplication(): Application? {
        return try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread")
            val activityThread = currentActivityThreadMethod.invoke(null)
            val getApplicationMethod = activityThreadClass.getMethod("getApplication")
            getApplicationMethod.invoke(activityThread) as? Application
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun logEvent(
        eventName: String,
        eventProperties: Map<String, Any> = emptyMap(),
    ) {
        scope.launch {
            amplitude.track(eventName, eventProperties)
        }
    }

    private fun setExperiment() {
        scope.launch {
            try {
                experimentClient.fetch().get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getExperimentVariant(key: String): JsonObject? {
        return try {
            val variant = experimentClient.variant(key)
            val payloadString = variant.payload.toString()
            parseVariantData(payloadString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseVariantData(payload: String): JsonObject {
        return try {
            JsonParser.parseString(payload).asJsonObject
        } catch (e: Exception) {
            e.printStackTrace()
            JsonObject()
        }
    }
}

