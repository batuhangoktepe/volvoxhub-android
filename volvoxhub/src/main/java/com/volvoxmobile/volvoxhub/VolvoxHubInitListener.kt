package com.volvoxmobile.volvoxhub

import com.volvoxmobile.volvoxhub.data.local.model.VolvoxHubResponse

/**
 * Listener for the hub init process
 * @see onInitCompleted called when the hub init process is completed
 * @see onInitFailed called when the hub init process is failed
 */
interface VolvoxHubInitListener {
    fun onInitCompleted(volvoxHubResponse: VolvoxHubResponse)
    fun onInitFailed(error: Int)
}