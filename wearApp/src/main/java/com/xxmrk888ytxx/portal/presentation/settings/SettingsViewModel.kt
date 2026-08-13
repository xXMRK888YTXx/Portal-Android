package com.xxmrk888ytxx.portal.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class SettingsViewModel @Inject constructor(
    private val wearPhoneGateway: WearPhoneGateway
) : ViewModel() {

    private val _isPhoneConnected = MutableStateFlow<Boolean?>(null)
    val isPhoneConnected: StateFlow<Boolean?> = _isPhoneConnected.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SettingsSideEffect>(extraBufferCapacity = 4)
    val sideEffect: SharedFlow<SettingsSideEffect> = _sideEffect.asSharedFlow()

    init {
        refreshPhoneConnection()
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.NavigateBack -> _sideEffect.tryEmit(SettingsSideEffect.NavigateBack)
            SettingsEvent.OpenNotificationSettings -> {
                _sideEffect.tryEmit(SettingsSideEffect.OpenNotificationSettings)
            }

            SettingsEvent.RefreshPhoneConnection -> refreshPhoneConnection()
        }
    }

    private fun refreshPhoneConnection() {
        viewModelScope.launch {
            _isPhoneConnected.value = runCatching {
                wearPhoneGateway.isPhoneAvailable()
            }.getOrDefault(false)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<SettingsViewModel>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}
