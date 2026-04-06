package com.xxmrk888ytxx.mainscreen.contract

import com.xxmrk888ytxx.mainscreen.exception.BiometricAuthFailedException
import com.xxmrk888ytxx.mainscreen.model.Device

interface SendUnlockRequestContract {
    @Throws(IllegalArgumentException::class, BiometricAuthFailedException::class)
    suspend fun unlock(device: Device): Result<Unit>
}