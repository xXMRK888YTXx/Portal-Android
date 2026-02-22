package com.xxmrk888ytxx.mainscreen.contract

import com.xxmrk888ytxx.mainscreen.model.Device

interface SendUnlockRequestContract {
    suspend fun unlock(device: Device): Result<Unit>
}