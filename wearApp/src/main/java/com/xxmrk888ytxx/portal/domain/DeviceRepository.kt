package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.flow.StateFlow

interface DeviceRepository {
    val devices: StateFlow<List<Device>>
    fun updateDevices(devices: List<Device>)
}
