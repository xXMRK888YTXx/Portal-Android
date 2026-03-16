package com.xxmrk888ytxx.portal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BluetoothPairBody(
    @SerialName("code") val pairCode: String,
    @SerialName("type") val type: String = "pair_request",
)
