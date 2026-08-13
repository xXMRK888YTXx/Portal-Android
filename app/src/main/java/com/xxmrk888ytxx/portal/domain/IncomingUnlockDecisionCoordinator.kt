package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.data.wear.WearDecisionPayloadValue
import com.xxmrk888ytxx.portal.data.wear.WearFinalStatusPayload
import kotlinx.coroutines.flow.Flow

interface IncomingUnlockDecisionCoordinator {
    val finalStatus: Flow<WearFinalStatusPayload>

    fun register(clientId: String, requestId: String?): String

    fun findDecisionId(clientId: String, requestId: String?): String?

    suspend fun resolve(decisionId: String, decision: WearDecisionPayloadValue): Boolean
}
