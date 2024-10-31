package com.hub.example.ui.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import com.volvoxmobile.volvoxhub.VolvoxHub
import com.volvoxmobile.volvoxhub.VolvoxHubInitListener
import com.volvoxmobile.volvoxhub.data.local.model.VolvoxHubResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor() : ViewModel() {
        private val _isBannedPopupVisible = MutableStateFlow(false)
        private val _setWebScreenVisible = MutableStateFlow(false)

        val isBannedPopupVisible: StateFlow<Boolean> = _isBannedPopupVisible
        val isWebScreenVisible: StateFlow<Boolean> = _setWebScreenVisible

        init {
            VolvoxHub.getInstance().startAuthorization(
                object : VolvoxHubInitListener {
                    override fun onInitCompleted(volvoxHubResponse: VolvoxHubResponse) {
                        Log.e("VolvoxHub", "onInitCompleted: $volvoxHubResponse")
                        _isBannedPopupVisible.value = volvoxHubResponse.banned
                    }

                    override fun onInitFailed(error: Int) {
                        Log.e("VolvoxHub", "onInitFailed: $error")
                    }
                },
            )
        }

        fun setWebScreenVisible(isVisible: Boolean) {
            _setWebScreenVisible.value = isVisible
        }
    }
