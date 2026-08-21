package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.portal.data.WearDataLayerProtocol.FINAL_STATUS_PATH
import com.xxmrk888ytxx.portal.data.WearDataLayerProtocol.INCOMING_UNLOCK_REQUEST_PATH
import com.xxmrk888ytxx.portal.data.WearDataLayerProtocol.PROFILES_PATH
import com.xxmrk888ytxx.portal.data.WearDataLayerProtocol.SYNC_DEVICES_REQUEST_PATH
import com.xxmrk888ytxx.portal.data.WearDataLayerProtocol.UNLOCK_COMMAND_PATH
import com.xxmrk888ytxx.portal.data.WearDataLayerProtocol.UNLOCK_REQUEST_DECISION_PATH
import com.xxmrk888ytxx.portal.data.WearDataLayerProtocol.WOL_UNLOCK_COMMAND_PATH
import kotlinx.serialization.Serializable

/**
 * Shared Wearable Data Layer contract used by the watch app.
 *
 * The same paths and payloads are declared on the phone in
 * `app/src/main/.../data/wear/WearDataLayerProtocol.kt` and must stay identical.
 * [PROFILES_PATH] is a persistent DataItem: the phone writes the latest device snapshot and the
 * watch reads it after startup, reinstall, reconnect, or a manual refresh. All other paths are
 * one-shot MessageClient events and are not queued while the phone is unavailable.
 *
 * Direction and lifecycle:
 * - [SYNC_DEVICES_REQUEST_PATH]: watch -> phone, asks for a fresh snapshot; the phone responds by
 *   writing [PROFILES_PATH].
 * - [UNLOCK_COMMAND_PATH]: watch -> phone, unlock the selected device through its configured
 *   transport.
 * - [WOL_UNLOCK_COMMAND_PATH]: watch -> phone, send Wake-on-LAN and then unlock a Wi-Fi device.
 * - [INCOMING_UNLOCK_REQUEST_PATH]: phone -> watch, forwards a PC-initiated request when the profile
 *   setting allows it; the watch stores the pending [WearIncomingUnlockPayload].
 * - [UNLOCK_REQUEST_DECISION_PATH]: watch -> phone, sends the user's first unlock/cancel decision.
 * - [FINAL_STATUS_PATH]: phone -> watch, broadcasts the result so every watch UI and notification
 *   closes the request, including decisions made on the phone or another watch.
 *
 * Only non-sensitive metadata and commands cross the Data Layer. Credentials, certificates, IP
 * addresses, and MAC addresses remain on the phone. [WearDecisionPayload.decisionId] prevents
 * duplicate decisions from multiple surfaces from being applied twice.
 */
object WearDataLayerProtocol {
    const val CAPABILITY_PHONE_APP = "portal_phone_app"
    const val CAPABILITY_WATCH_APP = "portal_watch_app"

    const val PROFILES_PATH = "/portal/profiles"
    const val PROFILE_PAYLOAD_KEY = "payload"

    const val UNLOCK_COMMAND_PATH = "/portal/command/unlock"
    const val WOL_UNLOCK_COMMAND_PATH = "/portal/command/wol_unlock"
    const val INCOMING_UNLOCK_REQUEST_PATH = "/portal/request/incoming"
    const val UNLOCK_REQUEST_DECISION_PATH = "/portal/request/decision"
    const val FINAL_STATUS_PATH = "/portal/request/final"
    const val SYNC_DEVICES_REQUEST_PATH = "/portal/devices/sync_request"
}

/**
 * Full device list snapshot received from the phone.
 */
@Serializable
data class WearDevicesPayload(
    val revision: Long = 0L,
    val devices: List<WearDevicePayload>
)

/**
 * Serializable transport-safe device metadata.
 */
@Serializable
data class WearDevicePayload(
    val clientId: String,
    val name: String,
    val transport: WearDeviceTransportPayload,
    val isWakeOnLanAvailable: Boolean
)

/**
 * Serializable transport enum mirrored from the phone protocol.
 */
@Serializable
enum class WearDeviceTransportPayload {
    WIFI,
    BLUETOOTH
}

/**
 * Command payload used when the watch asks the phone to unlock a synced device.
 */
@Serializable
data class WearUnlockCommandPayload(
    val clientId: String
)

/**
 * Incoming unlock request payload sent by the phone when a PC asks for user approval.
 */
@Serializable
data class WearIncomingUnlockPayload(
    val decisionId: String,
    val clientId: String,
    val deviceName: String
)

/**
 * User decision payload sent from the watch back to the phone.
 */
@Serializable
data class WearDecisionPayload(
    val decisionId: String,
    val decision: WearDecisionPayloadValue
)

/**
 * Possible user decisions for an incoming unlock request.
 */
@Serializable
enum class WearDecisionPayloadValue {
    UNLOCK,
    CANCEL
}

/**
 * Final request status broadcast by the phone to every watch UI/notification.
 */
@Serializable
data class WearFinalStatusPayload(
    val decisionId: String,
    val status: WearFinalStatusPayloadValue
)

/**
 * Final status values understood by the watch.
 */
@Serializable
enum class WearFinalStatusPayloadValue {
    UNLOCKED,
    CANCELED,
    ALREADY_COMPLETED,
    ERROR
}
