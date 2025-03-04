package com.volvoxmobile.volvoxhub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class HubColors(
    val topBarIconColor: Color = Color.Unspecified,
    val topBarTextColor: Color = Color.Unspecified,
    val topBarBackground: Color = Color.Unspecified,
)

val LocalHubColors = staticCompositionLocalOf {
    HubColors()
}

@Composable
fun HubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val customColors = if (darkTheme)
        HubColors(
            topBarIconColor = Color.White,
            topBarTextColor = Color.White,
            topBarBackground = Color.Black,
        )
    else HubColors(
        topBarIconColor = Color.Black,
        topBarTextColor = Color.Black,
        topBarBackground = Color.White,
    )

    CompositionLocalProvider(
        LocalHubColors provides customColors,
        content = content
    )
}

object HubTheme {
    val colors: HubColors
        @Composable
        get() = LocalHubColors.current
}
