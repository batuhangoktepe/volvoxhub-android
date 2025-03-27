package com.volvoxmobile.volvoxhub.ui.contact_us.contacts

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubColors
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme
import kotlinx.serialization.Serializable

@Serializable
data object ContactsRoute

fun NavGraphBuilder.contactsScreen(
    navigateToDetail: (String?, category: String) -> Unit,
    navigateBack: () -> Unit,
    fonts: HubFonts,
    darkColors: VolvoxHubColors,
    lightColors: VolvoxHubColors,
    isTitleCentered: Boolean,
    darkTheme:Boolean,
    setSystemBarsPadding: Boolean
) {
    composable<ContactsRoute>(
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(600)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(600)
            )
        }
    ) {
        VolvoxHubTheme(
            darkTheme = darkTheme,
            darkColors = darkColors,
            lightColors = lightColors
        ) {
            Contacts(
                navigateToDetail = navigateToDetail,
                fonts = fonts,
                navigateBack = navigateBack,
                isTitleCentered = isTitleCentered,
                setSystemBarsPadding = setSystemBarsPadding
            )
        }
    }
}
