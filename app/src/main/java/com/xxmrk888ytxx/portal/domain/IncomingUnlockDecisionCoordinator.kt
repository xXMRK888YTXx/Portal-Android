package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.data.wear.WearDecisionPayloadValue
import kotlinx.coroutines.flow.Flow

interface IncomingUnlockDecisionCoordinator {
    val finalStatus: Flow<IncomingUnlockFinalStatus>

    suspend fun register(clientId: String, requestId: String?): String

    suspend fun findDecisionId(clientId: String, requestId: String?): String?

    suspend fun resolve(decisionId: String, decision: WearDecisionPayloadValue): Boolean
}

data class IncomingUnlockFinalStatus(
    val decisionId: String,
    val clientId: String?,
)
