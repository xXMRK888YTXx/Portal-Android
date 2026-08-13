package com.xxmrk888ytxx.portal.presentation.deviceList

import com.xxmrk888ytxx.portal.domain.model.Device

sealed interface DeviceListEvent {
    data object OpenSettings : DeviceListEvent
    data object RefreshDevices : DeviceListEvent
    data object SilentRefreshDevices : DeviceListEvent
    data class SelectDevice(val device: Device) : DeviceListEvent
}
