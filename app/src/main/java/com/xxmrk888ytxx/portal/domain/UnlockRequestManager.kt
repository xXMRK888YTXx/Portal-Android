package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest
import com.xxmrk888ytxx.portal.domain.model.WifiDevice

interface UnlockRequestManager {
    fun automaticUnlock(
        deviceId: String,
        unlockOnlyWhenScreenUnlocked: Boolean,
        request: UnlockServiceRequest
    )
    fun showUnlockScreen(deviceId: String, deviceName: String, request: UnlockServiceRequest)
    fun sendNotification(
        deviceId: String,
        deviceName: String,
        request: UnlockServiceRequest
    )
}