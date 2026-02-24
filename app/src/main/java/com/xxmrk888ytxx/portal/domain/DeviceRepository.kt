package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    val devices: Flow<List<Device>>
    suspend fun saveDevice(device: Device)
    suspend fun getDeviceById(deviceId: String): Device?
    suspend fun removeDevice(deviceId: String)
}