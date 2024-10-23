package com.volvoxmobile.volvoxhub

import com.volvoxmobile.volvoxhub.common.util.VolvoxHubLogLevel
import com.volvoxmobile.volvoxhub.strings.ConfigureStrings

object VolvoxHubLogManager {
    private var volvoxHubLogLevel: VolvoxHubLogLevel = VolvoxHubLogLevel.INFO

    fun isDebug(): Boolean = volvoxHubLogLevel == VolvoxHubLogLevel.DEBUG

    fun log(
        message: String,
        level: VolvoxHubLogLevel,
    ) {
        if (shouldLog(level)) {
            printLog(level, message)
        }
    }

    fun setLogLevel(level: VolvoxHubLogLevel) {
        if (level == VolvoxHubLogLevel.DEBUG) {
            log(ConfigureStrings.DEBUG_ENABLED, VolvoxHubLogLevel.INFO)
        }
        volvoxHubLogLevel = level
    }

    private fun shouldLog(level: VolvoxHubLogLevel): Boolean =
        level == VolvoxHubLogLevel.ERROR || volvoxHubLogLevel == VolvoxHubLogLevel.DEBUG

    private fun printLog(
        level: VolvoxHubLogLevel,
        message: String,
    ) {
        val logPrefix =
            when (level) {
                VolvoxHubLogLevel.DEBUG -> "DEBUG"
                VolvoxHubLogLevel.INFO -> "INFO"
                VolvoxHubLogLevel.WARNING -> "WARNING"
                VolvoxHubLogLevel.ERROR -> "ERROR"
            }
        println("$logPrefix: $message")
    }
}
