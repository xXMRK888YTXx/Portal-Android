package com.xxmrk888ytxx.addnewdevicescreen.model

import com.xxmrk888ytxx.coreandroid.mvi.SideEffect

sealed interface AddNewDeviceScreenSideEffect : SideEffect {
    data class ScrollToPage(internal val pageId: Int) : AddNewDeviceScreenSideEffect
    object NavigationBack : AddNewDeviceScreenSideEffect
    object ToWifiConfigurationPage : AddNewDeviceScreenSideEffect
    object ToBluetoothConfigurationPage : AddNewDeviceScreenSideEffect
}