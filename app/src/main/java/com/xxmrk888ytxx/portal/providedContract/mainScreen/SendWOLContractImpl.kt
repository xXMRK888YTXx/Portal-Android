package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.SendWOLContract
import com.xxmrk888ytxx.mainscreen.model.Device
import javax.inject.Inject

class SendWOLContractImpl @Inject constructor() : SendWOLContract {
    override suspend fun sendRequest(
        device: Device,
        isTryToSendUnlockRequestEnabled: Boolean
    ): Result<Unit> {
        return Result.success(Unit)
    }
}