package com.xxmrk888ytxx.mainscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import com.xxmrk888ytxx.mainscreen.contract.ProvideSavedDevices
import com.xxmrk888ytxx.mainscreen.contract.SendUnlockRequestContract
import com.xxmrk888ytxx.mainscreen.model.Device
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.MainScreenSideEffect
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val provideSavedDevices: ProvideSavedDevices,
    private val unlockRequestContract: SendUnlockRequestContract,
) : SideEffectPortalViewModel<ScreenState, MainScreenEvent>(ScreenState()) {

    private val isLoading = MutableStateFlow(false)

    override val state: StateFlow<ScreenState> =
        combine(provideSavedDevices.devices, isLoading) { deviceList, isLoading ->
            ScreenState(deviceList, isLoading)
        }.stateWhileSubscribed()


    override fun handleEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.AddNewDevice -> sendNavigationAction { fromMainScreenToAddNewDeviceScreen() }
            is MainScreenEvent.SendUnlockRequest -> sendUnlockRequest(event.device)
            is MainScreenEvent.ToDeviceDetailsScreen -> sendNavigationAction { fromMainScreenToDeviceConfigurationScreen(event.deviceId) }
        }
    }

    private fun sendUnlockRequest(device: Device) {
        if (isLoading.value) return
        isLoading.value = true
        viewModelScope.launch { 
            unlockRequestContract.unlock(device)
                .onSuccess { sendToastSideEffect(uiText(R.string.device_unlocked)) }
                .onFailure {  sendToastSideEffect(uiText(R.string.failed_to_unlock_device)) }
        }.invokeOnCompletion { isLoading.value = false }
    }
}