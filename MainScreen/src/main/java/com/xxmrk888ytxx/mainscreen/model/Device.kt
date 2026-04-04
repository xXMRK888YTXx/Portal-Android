package com.xxmrk888ytxx.mainscreen.model

data class Device(
    val clientId: String,
    val host: String,
    val deviceName: String,
    val deviceType: DeviceType,
    val isHaveErrorsWithDevice: Boolean,
    val isWakeUpOnLanAvailable: Boolean
)

enum class DeviceType {
    WIFI, BLUETOOTH
}
