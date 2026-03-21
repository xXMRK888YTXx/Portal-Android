package com.xxmrk888ytxx.portal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WifiRemoteUnlockRequest(
    @SerialName("Type") val type: String,
    @SerialName("RequestId") val requestId: String?,
) {
    companion object {
        const val UNLOCK_REQUEST_TYPE = "unlock_request"
    }
}
