package com.xxmrk888ytxx.portal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object BluetoothRemoteUnlockMessage {

    @Serializable
    data class ApproveUnlockBluetooth(
        @SerialName("clientId") val clientId: String,
        @SerialName("type") val type: String = UNLOCK_RESPONSE_TYPE,
        @SerialName("status") val status: String = OK_STATUS,
        @SerialName("requestId") val requestId: String?,
    )

    @Serializable
    data class RejectUnlockBluetooth(
        @SerialName("clientId") val clientId: String,
        @SerialName("type") val type: String = UNLOCK_RESPONSE_TYPE,
        @SerialName("status") val status: String = REJECT_STATUS,
        @SerialName("requestId") val requestId: String?,
    )
    const val UNLOCK_RESPONSE_TYPE = "host_unlock_response"
    const val REJECT_STATUS = "rejected"
    const val OK_STATUS = "ok"
}

