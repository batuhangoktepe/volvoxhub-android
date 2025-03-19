package com.volvoxmobile.volvoxhub.ui.contact_us.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volvoxmobile.volvoxhub.R
import com.volvoxmobile.volvoxhub.common.extensions.safeClick
import com.volvoxmobile.volvoxhub.common.util.Localizations
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketsResponse
import com.volvoxmobile.volvoxhub.ui.contact_us.BaseBottomSheet
import com.volvoxmobile.volvoxhub.ui.contact_us.BaseHubTopBar
import com.volvoxmobile.volvoxhub.ui.contact_us.HubFonts
import com.volvoxmobile.volvoxhub.ui.theme.VolvoxHubTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun Contacts(
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = hiltViewModel<ContactsViewModel>(),
    navigateToDetail: (ticketId: String?, category: String) -> Unit,
    navigateBack: () -> Unit,
    fonts: HubFonts,
    isTitleCentered: Boolean
) {
    val tickets by viewModel.contactsUiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    var onNewTicket by rememberSaveable {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getTickets()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VolvoxHubTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BaseHubTopBar(
            title = Localizations.get(context,"contact_us"),
            titleFontFamily = fonts.semiBold,
            isTitleCentered = isTitleCentered,
            onNavigateBackClick = navigateBack,
            actionsButtons = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_pen),
                    tint = VolvoxHubTheme.colors.topBarIconColor,
                    contentDescription = "Edit",
                    modifier = Modifier.safeClick {
                        onNewTicket = true
                    }
                )
            },
            isSpacerVisible = true
        )
        when (tickets) {
            is ScreenUiState.Error -> {}
            ScreenUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = VolvoxHubTheme.colors.progressIndicatorColor
                    )
                }
            }

            is ScreenUiState.Success -> {
                if ((tickets as ScreenUiState.Success).data.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(top = 105.dp)
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                                text = Localizations.get(context,"you_dont_have_any_support_ticket"),
                                color = VolvoxHubTheme.colors.textColor,
                                fontFamily = fonts.regular,
                                fontSize = 14.sp
                            )
                        }
                        Button(
                            onClick = { onNewTicket = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonColors(
                                containerColor = VolvoxHubTheme.colors.newChatButtonColor,
                                contentColor = Color.White,
                                disabledContentColor = Color.Gray,
                                disabledContainerColor = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 24.dp),
                            contentPadding = PaddingValues(vertical = 14.dp)
                        ) {
                            Text(
                                text = Localizations.get(context,"create_new_ticket"),
                                fontFamily = fonts.bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val result = (tickets as ScreenUiState.Success).data
                        itemsIndexed(result) { index, ticket ->
                            ContactItem(
                                contactTitle = ticket.category ?: "",
                                contactDescription = ticket.lastMessage ?: "",
                                contactDate = formatTimestampToAmPm(ticket.lastMessageCreatedAt ?: ""),
                                isSeen = ticket.isSeen ?: false,
                                titleFamily = fonts.contactTitle,
                                descriptionFamily = fonts.contactDescription,
                                dateFamily = fonts.contactDate
                            ) {
                                navigateToDetail(ticket.id ?: "", ticket.category ?: "")
                            }
                            if (index < result.size - 1) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(VolvoxHubTheme.colors.topBarSpacer)
                                        .padding(horizontal = 24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        if (onNewTicket) {
            BaseBottomSheet(
                onDismissRequest = { onNewTicket = false },
                content = {
                    Column(
                        Modifier.padding(horizontal = 24.dp)
                    ) {
                        TICKETCATEGORIES.entries.forEachIndexed { index, category ->
                            Text(
                                text = Localizations.get(context, category.title),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                                    .padding(start = 16.dp)
                                    .safeClick {
                                        onNewTicket = false
                                        navigateToDetail(null, category.name)
                                    },
                                color = VolvoxHubTheme.colors.textColor,
                                fontFamily = fonts.regular,
                                fontSize = 14.sp
                            )

                            if (index < TICKETCATEGORIES.entries.size - 1) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(VolvoxHubTheme.colors.topBarSpacer)
                                )
                            }
                        }
                    }
                }
            )
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