package com.volvoxmobile.volvoxhub.ui.contact_us.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.volvoxmobile.volvoxhub.R
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketsResponse
import com.volvoxmobile.volvoxhub.ui.contact_us.BaseHubTopBar
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubColors
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme
import kotlinx.serialization.Serializable

@Serializable
data object ContactsRoute

fun NavGraphBuilder.contactsScreen(
    navigateToDetail: (String?) -> Unit,
    navigateBack: () -> Unit,
    fonts: HubFonts,
    darkColors: VolvoxHubColors,
    lightColors: VolvoxHubColors
) {
    composable<ContactsRoute> {
        VolvoxHubTheme(
            darkColors = darkColors,
            lightColors = lightColors
        ) {
            Contacts(
                navigateToDetail = navigateToDetail,
                fonts = fonts,
                navigateBack = navigateBack,
                topBarTitle = "Contact Us"
            )
        }
    }
}

@Composable
fun Contacts(
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = hiltViewModel<ContactsViewModel>(),
    navigateToDetail: (ticketId: String?) -> Unit,
    navigateBack: () -> Unit,
    fonts: HubFonts,
    topBarTitle: String
) {
    val tickets by viewModel.contactsUiState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier.fillMaxSize().background(VolvoxHubTheme.colors.background)
    ) {
        BaseHubTopBar(
            title = topBarTitle,
            titleFontFamily = fonts.semiBold,
            onNavigateBackClick = navigateBack,
            actionsButtons = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_pen),
                    tint = VolvoxHubTheme.colors.topBarIconColor,
                    contentDescription = "Edit",
                    modifier = Modifier.clickable {
                        navigateToDetail(null)
                    }
                )
            },
            isSpacerVisible = true
        )
        when (tickets) {
            is ScreenUiState.Error -> {}
            ScreenUiState.Loading -> {
                CircularProgressIndicator()
            }

            is ScreenUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items((tickets as ScreenUiState.Success<SupportTicketsResponse>).data) {
                        ContactItem(
                            contactTitle = it.lastMessage ?: "",
                            contactDescription = it.lastMessage ?: "",
                            contactDate = it.lastMessage ?: "",
                            titleFamily = fonts.contactTitle,
                            descriptionFamily = fonts.contactDescription,
                            dateFamily = fonts.contactDate
                        ) {
                            navigateToDetail(it.id ?: "")
                        }
                    }
                }
            }
        }
    }
}