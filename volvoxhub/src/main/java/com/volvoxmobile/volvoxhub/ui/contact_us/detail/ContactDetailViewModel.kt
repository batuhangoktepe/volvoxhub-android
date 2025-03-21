package com.volvoxmobile.volvoxhub.ui.contact_us.detail

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.volvoxmobile.volvoxhub.VolvoxHubService
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.CreateNewTicketResponse
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketResponse
import com.volvoxmobile.volvoxhub.ui.contact_us.contacts.ScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class ContactDetailViewModel @Inject constructor() : ViewModel() {

    var ticketId: String? = null

    private val _contactsDetailUiState =
        MutableStateFlow(
            ContactDetailUiState(
                messageList = emptyList(),
                screenState = ContactDetailScreenUiState.Loading,
                messageState = ContactDetailScreenUiState.Loading
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
                            screenState = ContactDetailScreenUiState.Error(errorMessage)
                        )
                    }
                },
                successCallback = { supportTicket ->
                    _contactsDetailUiState.update { contactDetailUiState ->
                        contactDetailUiState.copy(
                            screenState = ContactDetailScreenUiState.Success,
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

    fun sendMessage(message: String, category: String) {
        viewModelScope.launch {
            ticketId?.let {
                suspendCancellableCoroutine { continuation ->
                    VolvoxHubService.instance.createNewMessage(
                        ticketId = it,
                        message = message,
                        errorCallback = {},
                        successCallback = {
                            continuation.resume(Unit)
                        }
                    )
                }
            }

            if (ticketId.isNullOrBlank()) {
                val createNewTicketResponse = suspendCancellableCoroutine { continuation ->
                    VolvoxHubService.instance.createNewTicket(
                        category = category,
                        message = message,
                        errorCallback = { },
                        successCallback = { response ->
                            continuation.resume(response)
                        }
                    )
                }
                ticketId = createNewTicketResponse.id
            }

            _contactsDetailUiState.update {
                it.copy(
                    messageList = it.messageList + ContactMessageItem(
                        author = Author.USER,
                        message = message,
                        time = getCurrentFormattedTime()
                    )
                )
            }
        }
    }

    fun cleanScreenState() {
        viewModelScope.launch {
            _contactsDetailUiState.update {
                it.copy(
                    screenState = ContactDetailScreenUiState.Success
                )
            }
        }
    }
}

@Stable
data class ContactDetailUiState(
    val messageList: List<ContactMessageItem>,
    val screenState: ContactDetailScreenUiState,
    val messageState: ContactDetailScreenUiState
)

fun getCurrentFormattedTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date())
}

sealed interface ContactDetailScreenUiState {
    data object Loading : ContactDetailScreenUiState

    data class Error(val message: String? = null) : ContactDetailScreenUiState

    data object Success : ContactDetailScreenUiState
}

