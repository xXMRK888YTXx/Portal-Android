package com.xxmrk888ytxx.portal.domain

interface WOLServiceManager {
    suspend fun startWOLUnlock(deviceId: String, trySendUnlockRequests: Boolean)
}