package com.xxmrk888ytxx.addnewdevicescreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface AddNewDeviceScreenUiEvent : UiEvent {
    data object SelectedWifi: AddNewDeviceScreenUiEvent
    data object SelectedBluetooth: AddNewDeviceScreenUiEvent
    data class NextPage(val currentPage: Page): AddNewDeviceScreenUiEvent
    data class PreviousPage(val currentPage: Page): AddNewDeviceScreenUiEvent
    data class DeviceNameTextUpdated(val text: String) : AddNewDeviceScreenUiEvent
    data class HostTextUpdated(val text: String) : AddNewDeviceScreenUiEvent
    data class PairCodeTextUpdated(val text: String) : AddNewDeviceScreenUiEvent
    data object ConnectToDevice : AddNewDeviceScreenUiEvent
    data object FinishConfiguration : AddNewDeviceScreenUiEvent
    data object OnScanQrCodeClicked : AddNewDeviceScreenUiEvent
}