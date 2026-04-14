package com.xxmrk888ytxx.mainscreen.contract

import com.xxmrk888ytxx.mainscreen.model.Device

interface SendWOLContract {
    suspend fun sendRequest(device: Device, isTryToSendUnlockRequestEnabled: Boolean): Result<Unit>
}