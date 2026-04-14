package com.xxmrk888ytxx.portal.domain.model

data class WifiPairResult(
    val clientId: String,
    val certificateFingerprint: String,
    val macAddress: String?
)
