package com.xxmrk888ytxx.addnewdevicescreen

import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenSideEffect
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenUiEvent
import com.xxmrk888ytxx.addnewdevicescreen.model.Page
import com.xxmrk888ytxx.addnewdevicescreen.model.ScreenState
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import javax.inject.Inject

class AddNewDeviceViewModel @Inject constructor() : SideEffectPortalViewModel<ScreenState, AddNewDeviceScreenUiEvent, AddNewDeviceScreenSideEffect>(ScreenState.NoSelectedType) {

    override fun handleEvent(event: AddNewDeviceScreenUiEvent) {
        when(event) {
            is AddNewDeviceScreenUiEvent.SelectedBluetooth -> bluetoothSelected()
            is AddNewDeviceScreenUiEvent.SelectedWifi -> wifiSelected()
            is AddNewDeviceScreenUiEvent.NextPage -> nextPage(event.currentPage)
            is AddNewDeviceScreenUiEvent.PreviousPage -> previousPage(event.currentPage)
        }
    }

    private fun bluetoothSelected() {
        _state.value = ScreenState.Bluetooth()
    }

    private fun wifiSelected() {
        _state.value = ScreenState.Wifi()
    }

    private fun nextPage(currentPage: Page) {
        when(currentPage) {
            Page.SELECT_TYPE -> when(state.value) {
                is ScreenState.Bluetooth -> TODO()
                is ScreenState.Wifi -> sideEffect.tryEmit(AddNewDeviceScreenSideEffect.ToWifiConfigurationPage)
                ScreenState.NoSelectedType -> {}
            }
            else -> {}
        }
    }

    private fun previousPage(currentPage: Page) {
        when(currentPage.id) {
            0 -> sideEffect.tryEmit(AddNewDeviceScreenSideEffect.NavigationBack)
            else -> sideEffect.tryEmit(AddNewDeviceScreenSideEffect.ScrollToPage(currentPage.id - 1))
        }
    }
}