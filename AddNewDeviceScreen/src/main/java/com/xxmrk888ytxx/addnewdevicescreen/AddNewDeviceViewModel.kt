package com.xxmrk888ytxx.addnewdevicescreen

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToWifiDeviceContract
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenSideEffect
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenUiEvent
import com.xxmrk888ytxx.addnewdevicescreen.model.Page
import com.xxmrk888ytxx.addnewdevicescreen.model.ScreenState
import com.xxmrk888ytxx.addnewdevicescreen.model.Validator
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddNewDeviceViewModel @Inject constructor(
    private val connectToWifiDeviceContract: ConnectToWifiDeviceContract
) :
    SideEffectPortalViewModel<ScreenState, AddNewDeviceScreenUiEvent>(
        ScreenState.NoSelectedType
    ) {

    override fun handleEvent(event: AddNewDeviceScreenUiEvent) {
        when (event) {
            is AddNewDeviceScreenUiEvent.SelectedBluetooth -> bluetoothSelected()
            is AddNewDeviceScreenUiEvent.SelectedWifi -> wifiSelected()
            is AddNewDeviceScreenUiEvent.NextPage -> nextPage(event.currentPage)
            is AddNewDeviceScreenUiEvent.PreviousPage -> previousPage(event.currentPage)
            is AddNewDeviceScreenUiEvent.HostTextUpdated -> hostTextUpdated(event.text)
            is AddNewDeviceScreenUiEvent.PairCodeTextUpdated -> pairCodeUpdated(event.text)
            is AddNewDeviceScreenUiEvent.ConnectToDevice -> {
                when (val state = state.value) {
                    is ScreenState.Bluetooth -> TODO()
                    is ScreenState.Wifi -> connectToWifiDevice(state)
                    else -> {}
                }
            }

            AddNewDeviceScreenUiEvent.FinishConfiguration -> sendNavigateUpSideEffect()
            is AddNewDeviceScreenUiEvent.DeviceNameTextUpdated -> updateDeviceName(event.text)
        }
    }

    private fun connectToWifiDevice(value: ScreenState.Wifi) {
        updateLoadingState(true)
        viewModelScope.launch {
            connectToWifiDeviceContract.connect(value.host, value.pairCode)
                .onSuccess {
                    nextPage(Page.SUCCESS)
                }
                .onFailure {
                    sendToastSideEffect(uiText = uiText(R.string.unable_to_establish_connection))
                }
        }.invokeOnCompletion { updateLoadingState(false) }
    }

    private fun pairCodeUpdated(text: String) {
        if (text.length > 6 || !text.isDigitsOnly()) return
        updateWifiState { it.copy(pairCode = text) }
    }

    private fun hostTextUpdated(text: String) {
        val updatedText = text.replace(oldValue = ",", newValue = ".", ignoreCase = true)
        updateWifiState { it.copy(host = updatedText) }
    }

    private fun bluetoothSelected() {
        _state.value = ScreenState.Bluetooth()
    }

    private fun wifiSelected() {
        _state.value = ScreenState.Wifi()
    }

    private fun nextPage(currentPage: Page) {
        when (currentPage) {
            Page.SELECT_TYPE -> when (state.value) {
                is ScreenState.Bluetooth -> TODO()
                is ScreenState.Wifi -> sideEffect.tryEmit(AddNewDeviceScreenSideEffect.ToWifiConfigurationPage)
                ScreenState.NoSelectedType -> {}
            }

            Page.CONFIGURATION_WIFI -> sideEffect.tryEmit(AddNewDeviceScreenSideEffect.ToSuccessPage)
            Page.SUCCESS -> sideEffect.tryEmit(AddNewDeviceScreenSideEffect.ToSuccessPage)
        }
    }

    private fun previousPage(currentPage: Page) {
        when (currentPage.id) {
            0, Page.SUCCESS.id -> sendNavigateUpSideEffect()
            else -> sideEffect.tryEmit(AddNewDeviceScreenSideEffect.ScrollToPage(currentPage.id - 1))
        }
    }

    private fun updateWifiState(onUpdate: (ScreenState.Wifi) -> ScreenState.Wifi) {
        val currentState = _state.value as? ScreenState.Wifi ?: return
        val newState = onUpdate(currentState)
        _state.update { newState.copy(isDataValid = Validator.isWifiStateValid(newState)) }
    }

    private fun updateLoadingState(newState: Boolean) {
        when (val currentState = state.value) {
            is ScreenState.Bluetooth -> _state.update { currentState.copy(isLoading = newState) }
            is ScreenState.Wifi -> _state.update { currentState.copy(isLoading = newState) }
            is ScreenState.NoSelectedType -> {}
        }
    }

    private fun updateDeviceName(newName: String) {
        when (val currentState = state.value) {
            is ScreenState.Bluetooth -> _state.update { currentState.copy(deviceName = newName) }
            is ScreenState.Wifi -> _state.update { currentState.copy(deviceName = newName) }
            is ScreenState.NoSelectedType -> {}
        }
    }
}