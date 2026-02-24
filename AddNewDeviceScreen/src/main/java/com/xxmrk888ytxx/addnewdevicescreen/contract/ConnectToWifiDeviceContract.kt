package com.xxmrk888ytxx.addnewdevicescreen.contract

import com.xxmrk888ytxx.addnewdevicescreen.model.DeviceSettings
import kotlinx.coroutines.flow.Flow

interface ConnectToWifiDeviceContract {
    suspend fun connectAndProvideSettings(deviceName: String, host: String, pairCode: String): Result<Flow<DeviceSettings>>
}