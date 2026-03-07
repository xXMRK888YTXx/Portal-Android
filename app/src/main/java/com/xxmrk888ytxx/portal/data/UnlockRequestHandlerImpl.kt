package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.UnlockRequestHandler
import com.xxmrk888ytxx.portal.domain.UnlockScreenManager
import com.xxmrk888ytxx.portal.domain.UnlockServiceManager
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UnlockRequestHandlerImpl @Inject constructor(
    private val unlockServiceManager: UnlockServiceManager,
    private val unlockScreenManager: UnlockScreenManager,
    private val deviceRepository: DeviceRepository
) : UnlockRequestHandler {
    override suspend fun onNewRequest(
        clientId: String,
        request: UnlockServiceRequest
    ) {
        fastDebugLog("onNewRequest: $request")
        //unlockServiceManager.sendMessageToHost(clientId, UnlockServiceMessage.Unlock)
        val device = deviceRepository.getDeviceById(clientId).first() ?: return
        unlockScreenManager.showUnlockScreen(device)
    }
}