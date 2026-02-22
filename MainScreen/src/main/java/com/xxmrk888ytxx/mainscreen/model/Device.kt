package com.xxmrk888ytxx.mainscreen.model

data class Device(
    val deviceId: String,
    val name: String,
    val deviceType: DeviceType
)

enum class DeviceType {
    WIFI, BLUETOOTH
}
