package com.hub.example.ui.splash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.hub.example.R
import com.hub.example.ui.theme.AppTypography
import com.hub.example.ui.theme.HubTheme
import com.volvoxmobile.volvoxhub.VolvoxHub
import com.volvoxmobile.volvoxhub.common.util.Localizations
import com.volvoxmobile.volvoxhub.ui.ban.BannedPopupConfig
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HubTheme {
                SplashScreen(splashViewModel)
            }
        }
    }
}

@Composable
fun SplashScreen(splashViewModel: SplashViewModel) {
    val isBannedPopupVisible by splashViewModel.isBannedPopupVisible.collectAsState()
    val isWebScreenVisible by splashViewModel.isWebScreenVisible.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = Localizations.getHub(context = LocalContext.current, "Android"),
            modifier = Modifier.padding(innerPadding),
        )

        if (isBannedPopupVisible) {
            VolvoxHub.ShowBannedPopup(
                isVisible = true,
                config =
                    BannedPopupConfig(
                        titleText = "Banned",
                        imagePainter = painterResource(id = R.drawable.ic_ban),
                        titleTextStyle = AppTypography.titleMedium,
                        messageTextStyle = AppTypography.bodySmall,
                        buttonTextStyle = AppTypography.bodyMedium,
                    ),
            )
        }

        if (isWebScreenVisible) {
            VolvoxHub.ShowPrivacyPolicyScreen(context = LocalContext.current) {
                splashViewModel.setWebScreenVisible(false)
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HubTheme {
        Greeting("Andro")
    }
}
