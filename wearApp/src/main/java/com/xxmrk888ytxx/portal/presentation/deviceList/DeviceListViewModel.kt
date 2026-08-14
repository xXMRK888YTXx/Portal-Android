package com.xxmrk888ytxx.portal.presentation.deviceList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * MVI ViewModel for the synced device list.
 *
 * It observes cached devices and sends a phone sync request when the user refreshes the list.
 */
class DeviceListViewModel @Inject constructor(
    deviceRepository: DeviceRepository,
    private val wearPhoneGateway: WearPhoneGateway
) : ViewModel() {

    val devices: StateFlow<List<Device>> =
        deviceRepository.devices.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _sideEffect = MutableSharedFlow<DeviceListSideEffect>(extraBufferCapacity = 4)
    val sideEffect: SharedFlow<DeviceListSideEffect> = _sideEffect.asSharedFlow()

    fun handleEvent(event: DeviceListEvent) {
        when (event) {
            DeviceListEvent.OpenSettings -> {
                _sideEffect.tryEmit(DeviceListSideEffect.OpenSettings)
            }

            DeviceListEvent.RefreshDevices -> refresh(showError = true)
            DeviceListEvent.SilentRefreshDevices -> refresh(showError = false)
            is DeviceListEvent.SelectDevice -> {
                _sideEffect.tryEmit(DeviceListSideEffect.OpenDeviceActions(event.device))
            }
        }
    }

    private fun refresh(showError: Boolean) {
        viewModelScope.launch {
            runCatching { wearPhoneGateway.requestDeviceSync() }
                .onFailure {
                    if (showError) {
                        _sideEffect.tryEmit(DeviceListSideEffect.ShowRefreshError)
                    }
                }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<DeviceListViewModel>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}
