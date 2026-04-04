package com.xxmrk888ytxx.portal.data

import android.content.Context
import android.content.Intent
import com.xxmrk888ytxx.portal.data.service.ClientUnlockService
import com.xxmrk888ytxx.portal.data.service.WOLUnlockService
import com.xxmrk888ytxx.portal.data.service.model.ClientUnlockServiceParams
import com.xxmrk888ytxx.portal.domain.WOLServiceManager
import javax.inject.Inject

class WOLServiceManagerImpl @Inject constructor(
    private val context: Context
) : WOLServiceManager {

    override suspend fun startWOLUnlock(
        deviceId: String,
        trySendUnlockRequests: Boolean
    ) {
        val clientUnlockServiceParams = ClientUnlockServiceParams(
            clientId = deviceId,
            tryToRetryUnlockUntilSuccessOrTimeout = trySendUnlockRequests,
            isSendWOLRequest = true,
            isSendUnlockRequest = trySendUnlockRequests
        )
        val intent = Intent(context, WOLUnlockService::class.java).apply {
            putExtra(ClientUnlockService.CLIENT_UNLOCK_SERVICE_PARAMS_KEY,clientUnlockServiceParams)
            action = WOLUnlockService.WOL_UNLOCK_ACTION
        }
        context.startForegroundService(intent)
    }
}