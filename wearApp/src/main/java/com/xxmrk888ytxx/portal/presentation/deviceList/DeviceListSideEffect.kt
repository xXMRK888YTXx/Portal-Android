package com.xxmrk888ytxx.portal.presentation.deviceList

import com.xxmrk888ytxx.portal.domain.model.Device

/**
 * One-off effects emitted by [DeviceListViewModel] for host navigation or messages.
 */
sealed interface DeviceListSideEffect {
    data object OpenSettings : DeviceListSideEffect
    data class OpenDeviceActions(val device: Device) : DeviceListSideEffect
    data object ShowRefreshError : DeviceListSideEffect
}
