package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.WifiDevice
import kotlinx.coroutines.flow.Flow

interface WifiDeviceRepository {
    val devices: Flow<List<WifiDevice>>
    suspend fun saveDevice(wifiDevice: WifiDevice)
    fun getDeviceById(clientId: String): Flow<WifiDevice?>
    suspend fun updateHost(clientId: String, newHost: String)
    suspend fun updateDeviceName(clientId: String, newName: String)
    suspend fun updateWOLMacAddress(clientId: String, macAddress: String)
}