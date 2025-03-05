package com.volvoxmobile.volvoxhub.ui.contact_us.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
        modifier = modifier
            .fillMaxSize()
            .background(VolvoxHubTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BaseHubTopBar(
            title = topBarTitle,
            titleFontFamily = fonts.semiBold,
            isTitleCentered = true,
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
                if ((tickets as ScreenUiState.Success<SupportTicketsResponse>).data.isEmpty()) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_pen),
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .background(
                                color = VolvoxHubTheme.colors.editPenBackground,
                                shape = CircleShape
                            )
                            .padding(16.dp),
                        contentDescription = null,
                        tint = VolvoxHubTheme.colors.editPenTint
                    )
                    Text(
                        text = "Lorem ipsum dolar sit amet",
                        color = VolvoxHubTheme.colors.textColor,
                        fontFamily = fonts.regular
                    )
                    Button(
                        onClick = { navigateToDetail(null) },
                        shape = RectangleShape,
                        colors = ButtonColors(
                            containerColor = VolvoxHubTheme.colors.newChatButtonColor,
                            contentColor = VolvoxHubTheme.colors.textColor,
                            disabledContentColor = Color.Gray,
                            disabledContainerColor = Color.Gray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Text(
                            text = "New Chat",
                            fontFamily = fonts.bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items((tickets as ScreenUiState.Success<SupportTicketsResponse>).data) {
                            ContactItem(
                                contactTitle = it.category ?: "",
                                contactDescription = it.lastMessage ?: "",
                                contactDate = formatTimestampToAmPm(it.lastMessageCreatedAt ?: ""),
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
}

fun formatTimestampToAmPm(apiTimestamp: String): String {
    try {
        val isoFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        val date: Date = isoFormat.parse(apiTimestamp) ?: return ""
        val localTimeFormat = SimpleDateFormat("h:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
        return localTimeFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        return "Invalid date"
    }
}