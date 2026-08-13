package com.xxmrk888ytxx.portal.presentation.deviceList

import com.xxmrk888ytxx.portal.domain.model.Device

sealed interface DeviceListSideEffect {
    data object OpenSettings : DeviceListSideEffect
    data class OpenDeviceActions(val device: Device) : DeviceListSideEffect
    data object ShowRefreshError : DeviceListSideEffect
}
