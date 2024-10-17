package com.volvoxmobile.volvoxhub.common.util

import android.util.Log
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.volvoxmobile.volvoxhub.VolvoxHub
import com.volvoxmobile.volvoxhub.VolvoxHubLogManager
import com.volvoxmobile.volvoxhub.common.extensions.asHubApiException

suspend fun <T : Any> handleHubRequest(requestFunc: suspend () -> T): GenericResult<T> {
    return try {
        Ok(requestFunc.invoke())
    } catch (exception: Exception) {
        Err(exception.asHubApiException())
    }
}

inline fun <T> tryOrNull(block: () -> T): T? {
    return try {
        block()
    } catch (_: Exception) {
        null
    }
}

inline fun <T> tryOrLog(block: () -> T) {
    try {
        block()
    } catch (e: Exception) {
        e.let {
            Log.e("Localization", it.message.orEmpty())
            VolvoxHubLogManager.log(e.message.orEmpty(), VolvoxHubLogLevel.ERROR)
        }
    }
}