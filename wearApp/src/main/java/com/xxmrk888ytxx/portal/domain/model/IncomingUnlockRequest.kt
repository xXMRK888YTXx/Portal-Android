package com.xxmrk888ytxx.portal.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class IncomingUnlockRequest(
    val decisionId: String,
    val clientId: String,
    val deviceName: String,
    val isCompleted: Boolean = false
)
