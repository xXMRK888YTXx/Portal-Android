package com.xxmrk888ytxx.addnewdevicescreen.model

import com.xxmrk888ytxx.coreandroid.mvi.SideEffect

sealed interface AddNewDeviceScreenSideEffect : SideEffect {
    data class ScrollToPage(internal val pageId: Int) : AddNewDeviceScreenSideEffect
    data object ToWifiConfigurationPage : AddNewDeviceScreenSideEffect
    data object ToBluetoothConfigurationPage : AddNewDeviceScreenSideEffect
}