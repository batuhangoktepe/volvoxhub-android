package com.volvoxmobile.volvoxhub.ui.contact_us.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volvoxmobile.volvoxhub.common.util.Localizations
import com.volvoxmobile.volvoxhub.ui.contact_us.BaseHubTopBar
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
import com.volvoxmobile.volvoxhub.ui.contact_us.HubResources
import com.volvoxmobile.volvoxhub.ui.contact_us.contacts.ScreenUiState
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme

@Composable
fun ContactDetail(
    modifier: Modifier = Modifier,
    viewModel: ContactDetailViewModel = hiltViewModel<ContactDetailViewModel>(),
    navigateBack: () -> Unit,
    fonts: HubFonts,
    hubResources: HubResources,
    ticketId: String?,
    category: String,
    isTitleCentered: Boolean
) {
    val uiState by viewModel.contactsDetailUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        ticketId?.let {
            viewModel.getTicketHistory(ticketId)
            viewModel.ticketId = ticketId
        } ?: run {
            viewModel.cleanScreenState()
        }
    }

    Scaffold(
        topBar = {
            BaseHubTopBar(
                modifier = modifier,
                title = Localizations.get(context, "contact_us"),
                isTitleCentered = isTitleCentered,
                titleFontFamily = fonts.topBar,
                onNavigateBackClick = navigateBack
            )
        },
        bottomBar = {
            ContactMessageBar(
                onSendMessage = {
                    viewModel.sendMessage(it.message, category)
                },
                messageList = emptyList(),
                isTyping = false,
                fonts = fonts,
                hubResources = hubResources
            )
        },
        modifier = Modifier.background(VolvoxHubTheme.colors.background)
    ) { paddingValues ->
        when (uiState.screenState) {
            is ContactDetailScreenUiState.Error -> {
                Text(text = (uiState.screenState as ContactDetailScreenUiState.Error).message.toString())
            }

            ContactDetailScreenUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(VolvoxHubTheme.colors.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = VolvoxHubTheme.colors.progressIndicatorColor
                    )
                }
            }

            is ContactDetailScreenUiState.Success -> {
                ContactMessages(
                    Modifier.padding(paddingValues),
                    messageList = uiState.messageList,
                    fonts = fonts
                )
            }
        }
    }
}