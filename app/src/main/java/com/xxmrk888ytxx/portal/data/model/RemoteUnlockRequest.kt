package com.xxmrk888ytxx.portal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUnlockRequest(
    @SerialName("Type") val type: String,
    @SerialName("RequestId") val requestId: String?,
)
