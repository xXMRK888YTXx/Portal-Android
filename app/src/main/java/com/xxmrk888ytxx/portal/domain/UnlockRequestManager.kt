package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest

interface UnlockRequestManager {
    fun automaticUnlock(
        clientId: String,
        showUnlockScreenOrUnlockOnlyWhenScreenUnlocked: Boolean,
        request: UnlockServiceRequest
    )

    fun showUnlockScreen(
        clientId: String,
        deviceName: String,
        showUnlockScreenOrUnlockOnlyWhenScreenUnlocked: Boolean,
        request: UnlockServiceRequest
    )

    fun sendNotification(
        clientId: String,
        deviceName: String,
        request: UnlockServiceRequest,
        showUnlockScreenOrUnlockOnlyWhenScreenUnlocked: Boolean
    )
}