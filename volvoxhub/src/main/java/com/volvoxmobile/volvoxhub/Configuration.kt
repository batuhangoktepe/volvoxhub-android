package com.volvoxmobile.volvoxhub

import android.content.Context
import com.volvoxmobile.volvoxhub.common.util.ApiEnvironment
import java.util.Locale

class Configuration(
    val context: Context,
    val appName: String,
    val appId: String,
    val packageName: String,
    val environment: ApiEnvironment = ApiEnvironment.PROD,
    var languageCode: String = Locale.getDefault().language
)
