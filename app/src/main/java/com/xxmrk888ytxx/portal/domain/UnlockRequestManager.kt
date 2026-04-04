package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest

interface UnlockRequestManager {
    fun automaticUnlock(
        clientId: String,
        unlockOnlyWhenScreenUnlocked: Boolean,
        request: UnlockServiceRequest
    )
    fun showUnlockScreen(clientId: String, deviceName: String, request: UnlockServiceRequest)
    fun sendNotification(
        clientId: String,
        deviceName: String,
        request: UnlockServiceRequest
    )
}