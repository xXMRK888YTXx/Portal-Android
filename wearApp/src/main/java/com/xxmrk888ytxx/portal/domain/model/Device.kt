package com.xxmrk888ytxx.portal.domain.model

import kotlinx.serialization.Serializable

/**
 * Metadata-only PC profile synced from the phone to the watch.
 *
 * Keep this model intentionally small: no secrets, certificates, IP addresses, or MAC addresses
 * should be stored on the watch. Wake-on-LAN remains a phone-side operation.
 */
@Serializable
data class Device(
    val clientId: String,
    val name: String,
    val transport: DeviceTransport,
    val isWakeOnLanAvailable: Boolean
)

/**
 * Transport used by the phone for a synced device.
 */
@Serializable
enum class DeviceTransport {
    WIFI,
    BLUETOOTH
}
