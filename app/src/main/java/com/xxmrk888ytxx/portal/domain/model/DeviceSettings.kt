package com.xxmrk888ytxx.portal.domain.model

data class DeviceSettings(
    val deviceId: String,
    val awaitUnlockRequests: Boolean,
    val searchIpDynamically: Boolean,
    val unlockMethod: UnlockMethod,
    val unlockOnlyWhenScreenUnlocked: Boolean
)
