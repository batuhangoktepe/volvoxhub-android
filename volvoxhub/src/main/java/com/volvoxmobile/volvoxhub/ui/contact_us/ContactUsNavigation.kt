package com.volvoxmobile.volvoxhub.ui.contact_us

import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.volvoxmobile.volvoxhub.ui.contact_us.contacts.ContactsRoute
import com.volvoxmobile.volvoxhub.ui.contact_us.contacts.contactsScreen
import com.volvoxmobile.volvoxhub.ui.contact_us.detail.contactDetailScreen
import com.volvoxmobile.volvoxhub.ui.contact_us.detail.navigateToContactDetail
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubColors
import kotlinx.serialization.Serializable

data class HubFonts(
    val medium: FontFamily,
    val regular: FontFamily,
    val semiBold: FontFamily,
    val bold: FontFamily,
    val extraBold: FontFamily,
    val topBar: FontFamily,
    val message: FontFamily,
    val messageDate: FontFamily,
    val contactTitle: FontFamily,
    val contactDate: FontFamily,
    val contactDescription: FontFamily
)

@Serializable
data object ContactsScreensRoute

fun NavController.navigateToContactsScreens(
) = navigate(ContactsScreensRoute)

fun NavGraphBuilder.contactUsNavigation(
    navController: NavController,
    fonts: HubFonts,
    darkColors: VolvoxHubColors,
    lightColors: VolvoxHubColors
) {
    navigation<ContactsScreensRoute>(
        startDestination = ContactsRoute
    ) {
        contactsScreen(
            navigateToDetail = navController::navigateToContactDetail,
            navigateBack = navController::popBackStack,
            fonts = fonts,
            darkColors = darkColors,
            lightColors = lightColors
        )
        contactDetailScreen(
            navigateBack = navController::popBackStack,
            fonts = fonts,
            darkColors = darkColors,
            lightColors = lightColors
        )
    }
}