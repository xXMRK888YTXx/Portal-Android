package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.WifiDevice
import kotlinx.coroutines.flow.Flow

interface WifiDeviceRepository {
    val devices: Flow<List<WifiDevice>>
    suspend fun saveDevice(wifiDevice: WifiDevice)
    fun getDeviceById(deviceId: String): Flow<WifiDevice?>
    suspend fun removeDevice(deviceId: String)
    suspend fun updateHost(deviceId: String, newHost: String)
    suspend fun updateDeviceName(deviceId: String, newName: String)
}