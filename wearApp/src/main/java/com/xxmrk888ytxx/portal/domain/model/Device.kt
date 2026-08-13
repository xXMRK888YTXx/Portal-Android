package com.xxmrk888ytxx.portal.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val clientId: String,
    val name: String,
    val transport: DeviceTransport,
    val isWakeOnLanAvailable: Boolean
)

@Serializable
enum class DeviceTransport {
    WIFI,
    BLUETOOTH
}
