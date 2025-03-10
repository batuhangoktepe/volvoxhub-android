package com.volvoxmobile.volvoxhub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class Theme {
    LIGHT, DARK
}

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
    val newChatButtonColor: Color = Color.Unspecified,
    val textFieldBackground: Color = Color.Unspecified,
    val messageBarBorder: Color = Color.Unspecified,
    val textFieldPlaceholder: Color = Color.Unspecified,
    val isTypingColor: Color = Color.Unspecified,
    val textFieldImageBorder: Color = Color.Unspecified,
    val addButtonColor: Color = Color.Unspecified,
    val userMessageBackground: Color = Color.Unspecified,
    val userMessageTime: Color = Color.Unspecified,
    val userMessageText: Color = Color.Unspecified,
    val contactMessageBackground: Color = Color.Unspecified,
    val contactMessageTime: Color = Color.Unspecified,
    val contactMessageBorder: Color = Color.Unspecified,
    val progressIndicatorColor: Color = Color.Unspecified,
    val topBarSpacer: Color = Color.Unspecified
) {
    companion object {
        fun create(theme: Theme): VolvoxHubColors {
            return when (theme) {
                Theme.LIGHT -> VolvoxHubColors(
                    topBarIconColor = Color.Black,
                    topBarTextColor = Color.Black,
                    topBarBackground = Color.White,
                    textColor = Color.Black,
                    background = Color.White,
                    contactDescriptionColor = LightCaption,
                    contactDateColor = LightCaption,
                    editPenBackground = Color.Gray,
                    editPenTint = Color.White,
                    newChatButtonColor = Primary,
                    addButtonColor = Primary,
                    progressIndicatorColor = Primary,
                    messageBarBorder = LightBorder,
                    textFieldBackground = Light,
                    textFieldPlaceholder = LightCaption,
                    isTypingColor = LightCaption,
                    textFieldImageBorder = Primary,
                    userMessageBackground = Primary,
                    userMessageTime = LightChat,
                    contactMessageBackground = Color.White,
                    contactMessageTime = LightCaption,
                    contactMessageBorder = LightBorder,
                    topBarSpacer = LightBorder,
                    userMessageText = Color.White
                )

                Theme.DARK -> VolvoxHubColors(
                    topBarIconColor = Color.White,
                    topBarTextColor = Color.White,
                    topBarBackground = Color.Black,
                    textColor = Color.White,
                    background = Color.Black,
                    contactDescriptionColor = ContactDescriptionColor,
                    contactDateColor = ContactDescriptionColor,
                    editPenBackground = DarkGray,
                    editPenTint = Color.White,
                    newChatButtonColor = Primary,
                    addButtonColor = Primary,
                    userMessageBackground = Primary,
                    contactMessageBackground = Color.Black,
                    progressIndicatorColor = Primary,
                    messageBarBorder = SpacerGray,
                    textFieldBackground = Dark,
                    textFieldPlaceholder = Gray,
                    isTypingColor = Gray,
                    textFieldImageBorder = Primary,
                    userMessageTime = UserMessageTimeColor,
                    contactMessageTime = Gray,
                    contactMessageBorder = Line,
                    topBarSpacer = Line,
                    userMessageText = Color.White
                )
            }
        }
    }
}

val LocalHubColors = staticCompositionLocalOf {
    VolvoxHubColors()
}

@Composable
fun VolvoxHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    darkColors: VolvoxHubColors = VolvoxHubColors.create(Theme.DARK),
    lightColors: VolvoxHubColors = VolvoxHubColors.create(Theme.LIGHT),
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
