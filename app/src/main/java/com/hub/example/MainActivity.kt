package com.hub.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hub.example.ui.theme.HubTheme
import com.volvoxmobile.volvoxhub.Configuration
import com.volvoxmobile.volvoxhub.VolvoxHub
import com.volvoxmobile.volvoxhub.common.sign_in.GoogleSignIn
import com.volvoxmobile.volvoxhub.common.sign_in.GoogleSignInConfig
import com.volvoxmobile.volvoxhub.common.util.ApiEnvironment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HubTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        Modifier.padding(innerPadding),
                    ) {
                        Greeting(
                            name = "Android"
                        )
                        val configuration = Configuration(
                            context = applicationContext,
                            appName = "ComposeTemplate",
                            appId = "201b0e73-0482-4be7-ac16-d87cc325e1e2",
                            packageName = "com.stage.atom.ai",
                            environment = ApiEnvironment.STAGE,
                            languageCode = "en"
                        )
                        VolvoxHub.initialize(configuration)
                        GoogleSignIn.initialize(
                            config =
                            GoogleSignInConfig(
                                context = this@MainActivity,
                                serverClientId = "745784859284-n4ii5ptvfb3vk647ficbs8n9nicoaojl.apps.googleusercontent.com"
                            )
                        )
                        VolvoxHub.ShowLogInWithGoogle(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            successCallback = {

                            },
                            errorCallback = {
                                Log.d("TAG2", it.toString())
                            }
                        )
                    }

                }

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
        Greeting("Android")
    }
}
