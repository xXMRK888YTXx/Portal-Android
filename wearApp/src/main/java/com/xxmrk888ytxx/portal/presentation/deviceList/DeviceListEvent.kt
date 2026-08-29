package com.xxmrk888ytxx.portal.presentation.deviceList

import com.xxmrk888ytxx.portal.domain.model.Device

/**
 * User intents from the synced device list screen.
 */
sealed interface DeviceListEvent {
    data object OpenSettings : DeviceListEvent
    data object RefreshDevices : DeviceListEvent
    data object SilentRefreshDevices : DeviceListEvent
    data class SelectDevice(val device: Device) : DeviceListEvent
}
