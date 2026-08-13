package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest
import kotlinx.coroutines.flow.StateFlow

interface IncomingRequestRepository {
    val pendingRequest: StateFlow<IncomingUnlockRequest?>
    fun put(request: IncomingUnlockRequest)
    fun markCompleted(decisionId: String)
    fun clear(decisionId: String)
}
