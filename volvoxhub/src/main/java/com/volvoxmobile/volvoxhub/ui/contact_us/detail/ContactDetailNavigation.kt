package com.volvoxmobile.volvoxhub.ui.contact_us.detail

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
import com.volvoxmobile.volvoxhub.ui.contact_us.HubResources
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubColors
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme
import kotlinx.serialization.Serializable

@Serializable
data class ContactDetailRoute(
    val ticketId: String? = null
)

fun NavController.navigateToContactDetail(ticketId: String?) =
    navigate(ContactDetailRoute(ticketId))

fun NavGraphBuilder.contactDetailScreen(
    navigateBack: () -> Unit,
    fonts: HubFonts,
    darkColors: VolvoxHubColors,
    lightColors: VolvoxHubColors,
    hubResources: HubResources,
    isTitleCentered: Boolean
) {
    composable<ContactDetailRoute>(
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
        val arguments = it.toRoute<ContactDetailRoute>()
        VolvoxHubTheme(
            darkColors = darkColors,
            lightColors = lightColors
        ) {
            ContactDetail(
                navigateBack = navigateBack,
                fonts = fonts,
                hubResources = hubResources,
                ticketId = arguments.ticketId,
                isTitleCentered = isTitleCentered
            )
        }
    }
}