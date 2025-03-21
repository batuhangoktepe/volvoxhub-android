package com.volvoxmobile.volvoxhub.ui.contact_us

import androidx.annotation.DrawableRes
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.volvoxmobile.volvoxhub.R
import com.volvoxmobile.volvoxhub.ui.contact_us.contacts.ContactsRoute
import com.volvoxmobile.volvoxhub.ui.contact_us.contacts.contactsScreen
import com.volvoxmobile.volvoxhub.ui.contact_us.detail.contactDetailScreen
import com.volvoxmobile.volvoxhub.ui.contact_us.detail.navigateToContactDetail
import com.volvoxmobile.volvoxhub.ui.theme.Theme
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubColors
import com.volvoxmobile.volvoxhub.ui.theme.boldFont
import com.volvoxmobile.volvoxhub.ui.theme.extraBoldFont
import com.volvoxmobile.volvoxhub.ui.theme.mediumFont
import com.volvoxmobile.volvoxhub.ui.theme.regularFont
import com.volvoxmobile.volvoxhub.ui.theme.semiBoldFont
import kotlinx.serialization.Serializable

data class HubFonts(
    val medium: FontFamily = mediumFont,
    val regular: FontFamily = regularFont,
    val semiBold: FontFamily = semiBoldFont,
    val bold: FontFamily = boldFont,
    val extraBold: FontFamily = extraBoldFont,
    val topBar: FontFamily = semiBoldFont,
    val contactTitle: FontFamily = mediumFont,
    val contactDate: FontFamily = mediumFont,
    val contactDescription: FontFamily = regularFont,
    val messageText: FontFamily = mediumFont,
    val messageDateText: FontFamily = mediumFont,
)

data class HubResources(
    @DrawableRes
    val sendButton: Int = R.drawable.ic_send,
    @DrawableRes
    val disabledSendButton: Int = R.drawable.ic_disable_send,
    @DrawableRes
    val lightDisabledSendButton:Int = R.drawable.ic_light_disable_send
)

@Serializable
data object ContactsScreensRoute

fun NavController.navigateToContactsScreens(
) = navigate(ContactsScreensRoute)

fun NavGraphBuilder.contactUsNavigation(
    navController: NavController,
    fonts: HubFonts = HubFonts(),
    darkColors: VolvoxHubColors = VolvoxHubColors.create(Theme.DARK),
    lightColors: VolvoxHubColors = VolvoxHubColors.create(Theme.LIGHT),
    hubResources: HubResources = HubResources(),
    isTitleCentered: Boolean = false,
    darkTheme: Boolean = true
) {
    navigation<ContactsScreensRoute>(
        startDestination = ContactsRoute
    ) {
        contactsScreen(
            navigateToDetail = navController::navigateToContactDetail,
            navigateBack = navController::popBackStack,
            fonts = fonts,
            darkColors = darkColors,
            lightColors = lightColors,
            isTitleCentered = isTitleCentered,
            darkTheme = darkTheme
        )
        contactDetailScreen(
            navigateBack = navController::popBackStack,
            fonts = fonts,
            darkColors = darkColors,
            lightColors = lightColors,
            hubResources = hubResources,
            isTitleCentered = isTitleCentered,
            darkTheme = darkTheme
        )
    }
}