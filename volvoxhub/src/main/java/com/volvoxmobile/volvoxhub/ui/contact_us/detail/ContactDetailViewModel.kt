package com.volvoxmobile.volvoxhub.ui.contact_us.detail

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
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel @Inject constructor() : ViewModel() {

    private val _contactsDetailUiState =
        MutableStateFlow<ScreenUiState<SupportTicketResponse>>(ScreenUiState.Loading)
    val contactsDetailUiState = _contactsDetailUiState.asStateFlow()

    fun getTicket(ticketId: String) {
        viewModelScope.launch {
            VolvoxHubService.instance.getTicket(
                ticketId,
                errorCallback = { errorMessage ->
                    _contactsDetailUiState.update {
                        ScreenUiState.Error(errorMessage)
                    }
                },
                successCallback = { supportTicket ->
                    _contactsDetailUiState.update {
                        ScreenUiState.Success(supportTicket)
                    }
                }
            )
        }
    }
}