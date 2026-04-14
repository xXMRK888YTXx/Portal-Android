package com.xxmrk888ytxx.deviceconfigurationscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.SideEffect

interface DeviceConfigurationScreenSideEffect : SideEffect {
    data object OpenBluetoothSettings: DeviceConfigurationScreenSideEffect
}