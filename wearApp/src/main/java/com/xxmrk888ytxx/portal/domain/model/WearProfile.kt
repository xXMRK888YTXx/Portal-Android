package com.xxmrk888ytxx.portal.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WearProfile(
    val clientId: String,
    val name: String,
    val transport: WearTransport,
    val isWakeOnLanAvailable: Boolean
)

@Serializable
enum class WearTransport {
    WIFI,
    BLUETOOTH
}
