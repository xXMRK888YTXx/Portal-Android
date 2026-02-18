package com.xxmrk888ytxx.portal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PairBody(
    @SerialName("code") val pairCode: String
)