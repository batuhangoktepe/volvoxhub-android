package com.volvoxmobile.volvoxhub.ui.contact_us.detail

import android.annotation.SuppressLint
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
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketResponse
import com.volvoxmobile.volvoxhub.ui.contact_us.BaseHubTopBar
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
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
    lightColors: VolvoxHubColors
) {
    composable<ContactDetailRoute> {
        val arguments = it.toRoute<ContactDetailRoute>()
        VolvoxHubTheme(
            darkColors = darkColors,
            lightColors = lightColors
        ) {
            ContactDetail(navigateBack = navigateBack, fonts = fonts, ticketId = arguments.ticketId)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ContactDetail(
    modifier: Modifier = Modifier,
    viewModel: ContactDetailViewModel = hiltViewModel<ContactDetailViewModel>(),
    navigateBack: () -> Unit,
    fonts: HubFonts,
    ticketId: String?
) {
    val uiState by viewModel.contactsDetailUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        ticketId?.let {
            viewModel.getTicket(ticketId)
        }
    }

    Scaffold(
        topBar = {
            BaseHubTopBar(
                modifier = modifier,
                title = "Contact us",
                titleFontFamily = FontFamily.Serif,
                onNavigateBackClick = navigateBack,
                isSpacerVisible = true
            )
        },
        bottomBar = {}
    ) {
        when(uiState) {
            is ScreenUiState.Error -> {
                Text(text = (uiState as ScreenUiState.Error).message.toString())
            }
            ScreenUiState.Loading -> {
                CircularProgressIndicator()
            }
            is ScreenUiState.Success -> {
                Text(text = (uiState as ScreenUiState.Success<SupportTicketResponse>).data.id ?: "")
            }
        }
    }
}