package com.volvoxmobile.volvoxhub.ui.contact_us.detail

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.volvoxmobile.volvoxhub.VolvoxHubService
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketResponse
import com.volvoxmobile.volvoxhub.ui.contact_us.contacts.ScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel @Inject constructor() : ViewModel() {

    var ticketId: String? = null

    private val _contactsDetailUiState =
        MutableStateFlow(
            ContactDetailUiState(
                messageList = emptyList(),
                screenState = ScreenUiState.Loading,
                messageState = ScreenUiState.Loading
            )
        )

    val contactsDetailUiState = _contactsDetailUiState.asStateFlow()

    fun getTicketHistory(ticketId: String) {
        viewModelScope.launch {
            VolvoxHubService.instance.getTicket(
                ticketId,
                errorCallback = { errorMessage ->
                    _contactsDetailUiState.update {
                        it.copy(
                            screenState = ScreenUiState.Error(errorMessage)
                        )
                    }
                },
                successCallback = { supportTicket ->
                    _contactsDetailUiState.update { contactDetailUiState ->
                        contactDetailUiState.copy(
                            screenState = ScreenUiState.Success(supportTicket),
                            messageList = supportTicket.messages?.map {
                                ContactMessageItem(
                                    author = if (it?.isFromDevice == true) Author.USER else Author.GPT,
                                    message = it?.message ?: "",
                                    time = it?.createdAt,
                                )
                            }?.reversed() ?: emptyList()
                        )
                    }
                }
            )
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            if (ticketId.isNullOrBlank()) {
                VolvoxHubService.instance.createNewTicket(
                    category = "BILLING",
                    message = message,
                    errorCallback = {
                        Log.d("errorrss",it.toString())
                    },
                    successCallback = { createNewTicketResponse ->
                        ticketId = createNewTicketResponse.id
                    }
                )
            } else {
                ticketId?.let {
                    VolvoxHubService.instance.createNewMessage(
                        ticketId = it,
                        message = message,
                        errorCallback = {},
                        successCallback = {}
                    )
                }
            }
            ticketId?.let {
                _contactsDetailUiState.update {
                    it.copy(
                        messageList = it.messageList + ContactMessageItem(
                            author = Author.USER,
                            message = message,
                            mediaUri = null,
                            imageUrl = null,
                            time = getCurrentFormattedTime()
                        )
                    )
                }
            }
        }
    }
}

@Stable
data class ContactDetailUiState(
    val messageList: List<ContactMessageItem>,
    val screenState: ScreenUiState<SupportTicketResponse>,
    val messageState: ScreenUiState<SupportTicketResponse>
)

fun getCurrentFormattedTime(): String {
    val sdf = SimpleDateFormat("h:mm", Locale.getDefault())
    return sdf.format(Date())
}

