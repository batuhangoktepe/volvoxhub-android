package com.volvoxmobile.volvoxhub.ui.contact_us.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volvoxmobile.volvoxhub.VolvoxHubService
import com.volvoxmobile.volvoxhub.data.remote.model.hub.response.SupportTicketsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor() : ViewModel() {

    private val _contactsUiState =
        MutableStateFlow<ScreenUiState<SupportTicketsResponse>>(ScreenUiState.Loading)
    val contactsUiState = _contactsUiState.asStateFlow()

    init {
        getTickets()
    }

    fun getTickets() {
        viewModelScope.launch {
            VolvoxHubService.instance.getTickets(
                errorCallback = { errorMessage ->
                    _contactsUiState.update {
                        ScreenUiState.Error(errorMessage)
                    }
                },
                successCallback = { supportTicketsResponseItems ->
                    _contactsUiState.update {
                        ScreenUiState.Success(supportTicketsResponseItems)
                    }
                }
            )
        }
    }
}

sealed interface ScreenUiState<out T> {
    data object Loading : ScreenUiState<Nothing>

    data class Error(val message: String? = null) : ScreenUiState<Nothing>

    data class Success<T>(val data: T) : ScreenUiState<T>
}