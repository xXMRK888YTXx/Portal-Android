package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.mainscreen.contract.SendWOLContract
import com.xxmrk888ytxx.mainscreen.model.Device
import com.xxmrk888ytxx.portal.domain.WOLServiceManager
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SendWOLContractImpl @Inject constructor(
    private val wolServiceManager: WOLServiceManager
) : SendWOLContract {
    override suspend fun sendRequest(
        device: Device,
        isTryToSendUnlockRequestEnabled: Boolean
    ): Result<Unit> = runCatching(Dispatchers.Default) {
        wolServiceManager.startWOLUnlock(device.clientId, isTryToSendUnlockRequestEnabled)
    }
}