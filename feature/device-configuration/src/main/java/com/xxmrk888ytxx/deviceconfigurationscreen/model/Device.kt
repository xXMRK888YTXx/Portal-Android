package com.xxmrk888ytxx.deviceconfigurationscreen.model


sealed class Device(
    open val clientId: String,
    open val deviceName: String,
    open val awaitUnlockRequests: Boolean,
    open val unlockMethod: UnlockMethod,
    open val showUnlockScreenOrUnlockOnlyWhenScreenUnlocked: Boolean,
    open val forwardUnlockRequestsToWear: Boolean,
) {
    data class WifiDevice(
        override val clientId: String,
        override val deviceName: String,
        val host: String,
        val clientCertificateFingerprint: String,
        val serverCertificateFingerprint: String,
        override val awaitUnlockRequests: Boolean,
        val searchIpDynamically: Boolean,
        override val unlockMethod: UnlockMethod,
        val wolMacAddress: String?,
        override val showUnlockScreenOrUnlockOnlyWhenScreenUnlocked: Boolean,
        override val forwardUnlockRequestsToWear: Boolean
    ) : Device(
        clientId,
        deviceName,
        awaitUnlockRequests,
        unlockMethod,
        showUnlockScreenOrUnlockOnlyWhenScreenUnlocked,
        forwardUnlockRequestsToWear
    )

    data class BluetoothDevice(
        override val clientId: String,
        override val deviceName: String,
        val macAddress: String,
        override val awaitUnlockRequests: Boolean,
        override val unlockMethod: UnlockMethod,
        val isPaired: Boolean,
        override val showUnlockScreenOrUnlockOnlyWhenScreenUnlocked: Boolean,
        override val forwardUnlockRequestsToWear: Boolean
    ) : Device(
        clientId,
        deviceName,
        awaitUnlockRequests,
        unlockMethod,
        showUnlockScreenOrUnlockOnlyWhenScreenUnlocked,
        forwardUnlockRequestsToWear
    )
}
