package com.xxmrk888ytxx.portal.domain

interface AwaitUnlockRequestManager {
    suspend fun enableForDevice(clientId: String)
    suspend fun disableForDevice(clientId: String)
    fun restoreUnlockState()
}