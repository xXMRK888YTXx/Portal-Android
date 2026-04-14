package com.xxmrk888ytxx.portal.domain

interface WOLServiceManager {
    suspend fun startWOLUnlock(clientId: String, trySendUnlockRequests: Boolean)
}