package com.hub.example.ui

import android.app.Application
import com.volvoxmobile.volvoxhub.Configuration
import com.volvoxmobile.volvoxhub.VolvoxHub
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val volvoxHubConfiguration =
            Configuration(
                context = this,
                appName = "YOUR_PROJECT_NAME",
                appId = "YOUR_APP_ID",
                packageName = packageName,
            )
        VolvoxHub.initialize(volvoxHubConfiguration)
    }
}
