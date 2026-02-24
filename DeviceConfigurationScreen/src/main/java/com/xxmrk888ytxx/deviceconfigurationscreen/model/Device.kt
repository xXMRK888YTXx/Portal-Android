package com.xxmrk888ytxx.deviceconfigurationscreen.model

data class Device(
    val deviceId: String,
    val deviceName: String,
    val deviceType: DeviceType,
    val host: String,
    val clientCertificateFingerprint: String,
    val serverCertificateFingerprint: String
)

enum class DeviceType {
    WIFI, BLUETOOTH
}