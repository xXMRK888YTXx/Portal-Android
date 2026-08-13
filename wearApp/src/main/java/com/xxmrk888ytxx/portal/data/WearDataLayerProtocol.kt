package com.xxmrk888ytxx.portal.data

import kotlinx.serialization.Serializable

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
