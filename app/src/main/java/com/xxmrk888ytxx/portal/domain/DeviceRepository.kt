package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    suspend fun saveDevice(device: Device)
    val devices: Flow<List<Device>>
}