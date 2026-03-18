package com.xxmrk888ytxx.deviceconfigurationscreen.model


sealed class Device(
    open val deviceId: String,
    open val deviceName: String,
    open val awaitUnlockRequests: Boolean,
) {
    data class WifiDevice(
        override val deviceId: String,
        override val deviceName: String,
        val host: String,
        val clientCertificateFingerprint: String,
        val serverCertificateFingerprint: String,
        override val awaitUnlockRequests: Boolean,
        val searchIpDynamically: Boolean,
    ) : Device(deviceId, deviceName, awaitUnlockRequests)

    data class BluetoothDevice(
        override val deviceId: String,
        override val deviceName: String,
        val macAddress: String,
        override val awaitUnlockRequests: Boolean,
    ) : Device(deviceId, deviceName, awaitUnlockRequests)
}