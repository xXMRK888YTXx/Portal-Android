package com.xxmrk888ytxx.portal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


sealed interface WifiRemoteUnlockMessage{
    val clientId: String
    val type: String
    val status: String

    val requestId: String?

    @Serializable
    data class ApproveUnlockWifi(
        @SerialName("ClientId") override val clientId: String,
        @SerialName("Type") override val type: String = UNLOCK_RESPONSE_TYPE,
        @SerialName("Status") override val status: String = OK_STATUS,
        @SerialName("RequestId") override val requestId: String?,
    ) : WifiRemoteUnlockMessage

    @Serializable
    data class RejectUnlockWifi(
        @SerialName("ClientId") override val clientId: String,
        @SerialName("Type") override val type: String = UNLOCK_RESPONSE_TYPE,
        @SerialName("Status") override val status: String = REJECT_STATUS,
        @SerialName("RequestId") override val requestId: String?,
    ) : WifiRemoteUnlockMessage

    private companion object {
        const val UNLOCK_RESPONSE_TYPE = "unlock_response"
        const val REJECT_STATUS = "rejected"
        const val OK_STATUS = "ok"
    }
}
