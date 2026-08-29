package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest
import kotlinx.coroutines.flow.StateFlow

/**
 * Stores the pending incoming unlock request on the watch.
 *
 * The pending request is retained until the local user resolves it or the phone sends final status.
 * This allows stale notification taps to show a completed state instead of an empty screen.
 */
interface IncomingRequestRepository {
    val pendingRequest: StateFlow<IncomingUnlockRequest?>
    fun put(request: IncomingUnlockRequest)
    fun markCompleted(decisionId: String)
    fun clear(decisionId: String)
}
