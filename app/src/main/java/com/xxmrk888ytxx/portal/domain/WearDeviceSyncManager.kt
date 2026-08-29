package com.xxmrk888ytxx.portal.domain

interface WearDeviceSyncManager {
    fun startObserve()
    suspend fun syncNow()
}
