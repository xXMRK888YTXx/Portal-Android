package com.xxmrk888ytxx.portal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BluetoothUnlockRequest(
    @SerialName("clientId") val clientId: String,
    @SerialName("type") val type: String = TYPE,
) {
    companion object {
        const val TYPE = "unlock_request"
    }
}
