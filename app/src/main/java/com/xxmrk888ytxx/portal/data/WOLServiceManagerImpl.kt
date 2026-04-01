package com.xxmrk888ytxx.portal.data

import android.content.Context
import android.content.Intent
import com.xxmrk888ytxx.portal.data.service.WOLUnlockService
import com.xxmrk888ytxx.portal.domain.WOLServiceManager
import javax.inject.Inject

class WOLServiceManagerImpl @Inject constructor(
    private val context: Context
) : WOLServiceManager {

    override suspend fun startWOLUnlock(
        deviceId: String,
        trySendUnlockRequests: Boolean
    ) {
        val intent = Intent(context, WOLUnlockService::class.java).apply {
            putExtra(WOLUnlockService.DEVICE_ID_EXTRA, deviceId)
            putExtra(WOLUnlockService.TRY_SEND_UNLOCK_REQUEST_FLAG_ID,trySendUnlockRequests)
            action = WOLUnlockService.WOL_UNLOCK_ACTION
        }
        context.startForegroundService(intent)
    }
}