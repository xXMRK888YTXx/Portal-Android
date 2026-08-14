package com.xxmrk888ytxx.portal.presentation.deviceActions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * MVI ViewModel for commands available for a selected device.
 *
 * It delegates unlock and Wake-on-LAN unlock commands to the paired phone and reports the result as
 * side effects for the activity host to display.
 */
class DeviceActionsViewModel @Inject constructor(
    private val wearPhoneGateway: WearPhoneGateway
) : ViewModel() {

    private val _sideEffect = MutableSharedFlow<DeviceActionsSideEffect>(extraBufferCapacity = 4)
    val sideEffect: SharedFlow<DeviceActionsSideEffect> = _sideEffect.asSharedFlow()

    fun handleEvent(event: DeviceActionsEvent) {
        when (event) {
            DeviceActionsEvent.NavigateBack -> {
                _sideEffect.tryEmit(DeviceActionsSideEffect.NavigateBack)
            }

            is DeviceActionsEvent.Unlock -> {
                send { wearPhoneGateway.sendUnlockCommand(event.clientId) }
            }

            is DeviceActionsEvent.WakeOnLanUnlock -> {
                send { wearPhoneGateway.sendWakeOnLanUnlockCommand(event.clientId) }
            }
        }
    }

    private fun send(block: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { block() }
                .onSuccess { _sideEffect.tryEmit(DeviceActionsSideEffect.ShowCommandSent) }
                .onFailure { _sideEffect.tryEmit(DeviceActionsSideEffect.ShowCommandError) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<DeviceActionsViewModel>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}
