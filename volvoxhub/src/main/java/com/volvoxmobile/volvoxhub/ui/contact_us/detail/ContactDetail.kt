package com.volvoxmobile.volvoxhub.ui.contact_us.detail

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
import com.volvoxmobile.volvoxhub.ui.contact_us.BaseHubTopBar
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
import com.volvoxmobile.volvoxhub.ui.contact_us.HubResources
import com.volvoxmobile.volvoxhub.ui.contact_us.contacts.ScreenUiState

@Composable
fun ContactDetail(
    modifier: Modifier = Modifier,
    viewModel: ContactDetailViewModel = hiltViewModel<ContactDetailViewModel>(),
    navigateBack: () -> Unit,
    fonts: HubFonts,
    hubResources: HubResources,
    ticketId: String?,
    isTitleCentered: Boolean
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