package com.xxmrk888ytxx.portal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


object WifiRemoteUnlockMessage{

    @Serializable
    data class ApproveUnlockWifi(
        @SerialName("ClientId") val clientId: String,
        @SerialName("Type") val type: String = UNLOCK_RESPONSE_TYPE,
        @SerialName("Status") val status: String = OK_STATUS,
        @SerialName("RequestId") val requestId: String?,
    )

    @Serializable
    data class RejectUnlockWifi(
        @SerialName("ClientId") val clientId: String,
        @SerialName("Type") val type: String = UNLOCK_RESPONSE_TYPE,
        @SerialName("Status") val status: String = REJECT_STATUS,
        @SerialName("RequestId") val requestId: String?,
    )

    private const val UNLOCK_RESPONSE_TYPE = "unlock_response"
    private const val REJECT_STATUS = "rejected"
    private const val OK_STATUS = "ok"
}
