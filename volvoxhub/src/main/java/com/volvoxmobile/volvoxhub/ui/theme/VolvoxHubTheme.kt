package com.volvoxmobile.volvoxhub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class VolvoxHubColors(
    val topBarIconColor: Color = Color.Unspecified,
    val topBarTextColor: Color = Color.Unspecified,
    val topBarBackground: Color = Color.Unspecified,
    val textColor: Color = Color.Unspecified,
    val background: Color = Color.Unspecified,
    val contactDescriptionColor: Color = Color.Unspecified,
    val contactDateColor: Color = Color.Unspecified,
    val editPenBackground: Color = Color.Unspecified,
    val editPenTint: Color = Color.Unspecified,
    val newChatButtonColor: Color = Color.Unspecified
)

val LocalHubColors = staticCompositionLocalOf {
    VolvoxHubColors()
}

@Composable
fun VolvoxHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    darkColors: VolvoxHubColors,
    lightColors: VolvoxHubColors,
    content: @Composable () -> Unit
) {
    val customColors = if (darkTheme) darkColors
    else lightColors

    CompositionLocalProvider(
        LocalHubColors provides customColors,
        content = content
    )
}

object VolvoxHubTheme {
    val colors: VolvoxHubColors
        @Composable
        get() = LocalHubColors.current
}
