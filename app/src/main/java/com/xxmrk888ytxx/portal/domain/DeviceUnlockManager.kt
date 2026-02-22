package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.Device

interface DeviceUnlockManager {
    suspend fun unlockWifiDevice(device: Device): Result<Unit>
}