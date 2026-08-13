package com.xxmrk888ytxx.portal.presentation.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xxmrk888ytxx.portal.domain.WearPermissionChecker
import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Provider

class MainActivityViewModel @Inject constructor(
    private val permissionChecker: WearPermissionChecker
) : ViewModel() {

    private val _state = MutableStateFlow(
        MainScreenState(permissions = permissionChecker.getState())
    )
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<NavigationSideEffect>(extraBufferCapacity = 4)
    val sideEffect: SharedFlow<NavigationSideEffect> = _sideEffect.asSharedFlow()

    fun handleEvent(event: MainActivityEvent) {
        when (event) {
            MainActivityEvent.RefreshPermissions -> refreshPermissions()
            MainActivityEvent.ShowDevices -> showDevices()
            is MainActivityEvent.ShowDeviceActions -> showDeviceActions(event.device)
            MainActivityEvent.ShowSettings -> showSettings()
            MainActivityEvent.ShowIncomingRequest -> showIncomingRequest()
            MainActivityEvent.OpenNotificationSettings -> openNotificationSettings()
            is MainActivityEvent.ShowMessage -> showMessage(event.message)
        }
    }

    private fun refreshPermissions() {
        _state.update { it.copy(permissions = permissionChecker.getState()) }
    }

    private fun showDevices() {
        _state.update { it.copy(screen = WearScreen.Main, selectedDevice = null) }
    }

    private fun showDeviceActions(device: Device) {
        _state.update { it.copy(screen = WearScreen.DeviceActions, selectedDevice = device) }
    }

    private fun showSettings() {
        _state.update { it.copy(screen = WearScreen.Settings) }
    }

    private fun showIncomingRequest() {
        _state.update { it.copy(screen = WearScreen.IncomingRequest) }
    }

    private fun openNotificationSettings() {
        _sideEffect.tryEmit(NavigationSideEffect.OpenNotificationSettings)
    }

    private fun showMessage(message: String) {
        _sideEffect.tryEmit(NavigationSideEffect.ShowMessage(message))
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<MainActivityViewModel>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}
