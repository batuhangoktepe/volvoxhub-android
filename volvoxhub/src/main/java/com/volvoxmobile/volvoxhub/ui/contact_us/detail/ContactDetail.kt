package com.volvoxmobile.volvoxhub.ui.contact_us.detail

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.volvoxmobile.volvoxhub.ui.contact_us.BaseHubTopBar
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
import com.volvoxmobile.volvoxhub.ui.contact_us.HubResources
import com.volvoxmobile.volvoxhub.ui.contact_us.contacts.ScreenUiState
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
    hubResources: HubResources
) {
    composable<ContactDetailRoute> (
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
    ){
        val arguments = it.toRoute<ContactDetailRoute>()
        VolvoxHubTheme(
            darkColors = darkColors,
            lightColors = lightColors
        ) {
            ContactDetail(
                navigateBack = navigateBack,
                fonts = fonts,
                hubResources = hubResources,
                ticketId = arguments.ticketId
            )
        }
    }
}

@Composable
fun ContactDetail(
    modifier: Modifier = Modifier,
    viewModel: ContactDetailViewModel = hiltViewModel<ContactDetailViewModel>(),
    navigateBack: () -> Unit,
    fonts: HubFonts,
    hubResources: HubResources,
    ticketId: String?
) {
    val uiState by viewModel.contactsDetailUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        ticketId?.let {
            viewModel.getTicketHistory(ticketId)
            viewModel.ticketId = ticketId
        }
    }

    Scaffold(
        topBar = {
            BaseHubTopBar(
                modifier = modifier,
                title = "Contact us",
                isTitleCentered = true,
                titleFontFamily = FontFamily.Serif,
                onNavigateBackClick = navigateBack
            )
        },
        bottomBar = {
            ContactMessageBar(
                onSendMessage = {
                    viewModel.sendMessage(it.message)
                },
                messageList = emptyList(),
                isTyping = false,
                fonts = fonts,
                hubResources = hubResources
            )
        }
    ) { paddingValues ->
        when (uiState.screenState) {
            is ScreenUiState.Error -> {
                Text(text = ((uiState.screenState as ScreenUiState.Error).message.toString()))
            }

            ScreenUiState.Loading -> {
                CircularProgressIndicator()
            }

            is ScreenUiState.Success -> {
                ContactMessages(
                    Modifier.padding(paddingValues),
                    messageList = uiState.messageList,
                    fonts = fonts
                )
            }
        }
    }
}