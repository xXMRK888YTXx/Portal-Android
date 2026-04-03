package com.xxmrk888ytxx.portal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PairResponse(
    @SerialName("clientId") val clientId: String,
    @SerialName("macAddress") val macAddress: String?
)
