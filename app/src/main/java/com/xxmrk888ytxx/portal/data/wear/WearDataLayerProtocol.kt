package com.xxmrk888ytxx.portal.data.wear

import com.xxmrk888ytxx.portal.data.wear.WearDataLayerProtocol.DECISION_PATH
import com.xxmrk888ytxx.portal.data.wear.WearDataLayerProtocol.FINAL_STATUS_PATH
import com.xxmrk888ytxx.portal.data.wear.WearDataLayerProtocol.INCOMING_REQUEST_PATH
import com.xxmrk888ytxx.portal.data.wear.WearDataLayerProtocol.PROFILES_PATH
import com.xxmrk888ytxx.portal.data.wear.WearDataLayerProtocol.SYNC_DEVICES_REQUEST_PATH
import com.xxmrk888ytxx.portal.data.wear.WearDataLayerProtocol.UNLOCK_COMMAND_PATH
import com.xxmrk888ytxx.portal.data.wear.WearDataLayerProtocol.WOL_UNLOCK_COMMAND_PATH
import kotlinx.serialization.Serializable

/**
 * Phone-side mirror of the Wear OS Data Layer contract.
 *
 * [PROFILES_PATH] is the only persistent DataItem. The phone writes the latest
 * [WearDevicesPayload] snapshot there whenever devices change and when it receives
 * [SYNC_DEVICES_REQUEST_PATH] from a watch. The remaining paths are one-shot MessageClient
 * events; they are delivered immediately and are not queued if the peer is offline.
 *
 * Message flow:
 * - [UNLOCK_COMMAND_PATH] and [WOL_UNLOCK_COMMAND_PATH] are watch -> phone commands. The phone
 *   performs the existing unlock flow, optionally sending Wake-on-LAN first for Wi-Fi.
 * - [INCOMING_REQUEST_PATH] is phone -> watch when a PC asks for approval and the profile is
 *   configured to forward requests to Wear.
 * - [DECISION_PATH] is watch -> phone and carries the user's decision. The phone accepts only the
 *   first decision for a [WearDecisionPayload.decisionId].
 * - [FINAL_STATUS_PATH] is phone -> watch and closes all corresponding screens and notifications,
 *   including those belonging to other watches or the phone UI.
 *
 * The protocol deliberately transfers only device metadata and request identifiers. Secrets,
 * certificates, network addresses, and MAC addresses stay on the phone.
 */
object WearDataLayerProtocol {
    const val PROFILES_PATH = "/portal/profiles"
    const val PROFILE_PAYLOAD_KEY = "payload"

    const val UNLOCK_COMMAND_PATH = "/portal/command/unlock"
    const val WOL_UNLOCK_COMMAND_PATH = "/portal/command/wol_unlock"
    const val INCOMING_REQUEST_PATH = "/portal/request/incoming"
    const val DECISION_PATH = "/portal/request/decision"
    const val FINAL_STATUS_PATH = "/portal/request/final"
    const val SYNC_DEVICES_REQUEST_PATH = "/portal/devices/sync_request"
}

@Serializable
data class WearDevicesPayload(
    val revision: Long = 0L,
    val devices: List<WearDevicePayload>
)

@Serializable
data class WearDevicePayload(
    val clientId: String,
    val name: String,
    val transport: WearDeviceTransportPayload,
    val isWakeOnLanAvailable: Boolean
)

@Serializable
enum class WearDeviceTransportPayload {
    WIFI,
    BLUETOOTH
}

@Serializable
data class WearUnlockCommandPayload(
    val clientId: String
)

@Serializable
data class WearIncomingUnlockPayload(
    val decisionId: String,
    val clientId: String,
    val deviceName: String
)

@Serializable
data class WearDecisionPayload(
    val decisionId: String,
    val decision: WearDecisionPayloadValue
)

@Serializable
enum class WearDecisionPayloadValue {
    UNLOCK,
    CANCEL
}

@Serializable
data class WearFinalStatusPayload(
    val decisionId: String,
    val status: WearFinalStatusPayloadValue
)

@Serializable
enum class WearFinalStatusPayloadValue {
    UNLOCKED,
    CANCELED,
    ALREADY_COMPLETED,
    ERROR
}
