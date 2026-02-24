package com.xxmrk888ytxx.deviceconfigurationscreen.contract

import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device

interface ProvideDeviceInfoContract {
    suspend fun provideDeviceInfo(deviceId: String): Result<Device>
}