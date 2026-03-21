package com.xxmrk888ytxx.portal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BluetoothRemoteUnlockRequest(
    @SerialName("type") val type: String,
    @SerialName("requestId") val requestId: String?,
) {
    companion object {
        const val UNLOCK_REQUEST_TYPE = "host_unlock_request"
    }
}
