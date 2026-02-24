package com.xxmrk888ytxx.deviceconfigurationscreen.contract

import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device
import kotlinx.coroutines.flow.Flow

interface ProvideDeviceInfoContract {
    suspend fun provideDeviceInfo(deviceId: String): Flow<Device>
}