package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.UnlockRequestHandler
import com.xxmrk888ytxx.portal.domain.UnlockServiceManager
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest
import javax.inject.Inject

class UnlockRequestHandlerImpl @Inject constructor(
    private val unlockServiceManager: UnlockServiceManager
) : UnlockRequestHandler {
    override suspend fun onNewRequest(
        clientId: String,
        request: UnlockServiceRequest
    ) {
        fastDebugLog("onNewRequest: $request")
        unlockServiceManager.sendMessageToHost(clientId, UnlockServiceMessage.Unlock)
    }
}