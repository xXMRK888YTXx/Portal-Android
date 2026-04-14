package com.xxmrk888ytxx.portal.domain

interface DeviceRepository {
    suspend fun removeDevice(deviceId: String)
    suspend fun removeAllDevices()
}