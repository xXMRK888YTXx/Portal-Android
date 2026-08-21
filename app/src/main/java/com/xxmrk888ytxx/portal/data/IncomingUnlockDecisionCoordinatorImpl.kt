package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.portal.data.wear.WearDecisionPayloadValue
import com.xxmrk888ytxx.portal.data.wear.WearFinalStatusPayloadValue
import com.xxmrk888ytxx.portal.domain.IncomingUnlockDecisionCoordinator
import com.xxmrk888ytxx.portal.domain.IncomingUnlockFinalStatus
import com.xxmrk888ytxx.portal.domain.UnlockMessageSender
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject

class IncomingUnlockDecisionCoordinatorImpl @Inject constructor(
    private val unlockMessageSender: UnlockMessageSender,
    private val wearPhoneGateway: WearPhoneGateway
) : IncomingUnlockDecisionCoordinator {

    private val mutex = Mutex()
    private val pendingRequests = mutableMapOf<String, PendingRequest>()
    private val requestIdIndex = mutableMapOf<String, String>()
    private val completedDecisionIds =
        object : LinkedHashMap<String, Boolean>(MAX_COMPLETED_HISTORY, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Boolean>?): Boolean {
                return size > MAX_COMPLETED_HISTORY
            }
        }

    private val _finalStatus = MutableSharedFlow<IncomingUnlockFinalStatus>(
        extraBufferCapacity = 8
    )
    override val finalStatus: Flow<IncomingUnlockFinalStatus> = _finalStatus

    override suspend fun register(clientId: String, requestId: String?): String {
        return mutex.withLock {
            evictExpiredLocked()

            val indexKey = requestId?.let { "$clientId:$it" }
            if (indexKey != null) {
                requestIdIndex[indexKey]?.let { existingDecisionId ->
                    if (pendingRequests.containsKey(existingDecisionId)) {
                        return@withLock existingDecisionId
                    }
                }
            }

            val decisionId = UUID.randomUUID().toString()
            pendingRequests[decisionId] = PendingRequest(
                clientId = clientId,
                requestId = requestId,
                createdAt = System.currentTimeMillis()
            )
            if (indexKey != null) {
                requestIdIndex[indexKey] = decisionId
            }
            decisionId
        }
    }

    override suspend fun findDecisionId(clientId: String, requestId: String?): String? {
        if (requestId == null) return null
        return mutex.withLock {
            evictExpiredLocked()
            val decisionId = requestIdIndex["$clientId:$requestId"]
            if (decisionId != null && pendingRequests.containsKey(decisionId)) {
                decisionId
            } else {
                null
            }
        }
    }

    override suspend fun resolve(decisionId: String, decision: WearDecisionPayloadValue): Boolean {
        val resolveResult = mutex.withLock {
            evictExpiredLocked()
            when {
                completedDecisionIds.containsKey(decisionId) -> ResolveResult.AlreadyCompleted
                else -> {
                    completedDecisionIds[decisionId] = true
                    val removed = pendingRequests.remove(decisionId)
                    if (removed?.requestId != null) {
                        requestIdIndex.remove("${removed.clientId}:${removed.requestId}")
                    }
                    removed?.let(ResolveResult::Ready) ?: ResolveResult.AlreadyCompleted
                }
            }
        }

        val pendingRequest = when (resolveResult) {
            ResolveResult.AlreadyCompleted -> {
                sendFinalStatus(
                    decisionId = decisionId,
                    status = WearFinalStatusPayloadValue.ALREADY_COMPLETED,
                    clientId = null
                )
                return false
            }

            is ResolveResult.Ready -> resolveResult.pendingRequest
        }

        val message = when (decision) {
            WearDecisionPayloadValue.UNLOCK -> UnlockServiceMessage.Unlock(pendingRequest.requestId)
            WearDecisionPayloadValue.CANCEL -> UnlockServiceMessage.Canceled(pendingRequest.requestId)
        }
        unlockMessageSender.sendMessage(pendingRequest.clientId, message)

        val status = when (decision) {
            WearDecisionPayloadValue.UNLOCK -> WearFinalStatusPayloadValue.UNLOCKED
            WearDecisionPayloadValue.CANCEL -> WearFinalStatusPayloadValue.CANCELED
        }
        sendFinalStatus(
            decisionId = decisionId,
            status = status,
            clientId = pendingRequest.clientId
        )
        return true
    }

    private fun evictExpiredLocked(now: Long = System.currentTimeMillis()) {
        val iterator = pendingRequests.entries.iterator()
        while (iterator.hasNext()) {
            val (_, req) = iterator.next()
            if (now - req.createdAt > REQUEST_TTL_MS) {
                iterator.remove()
                if (req.requestId != null) {
                    requestIdIndex.remove("${req.clientId}:${req.requestId}")
                }
            }
        }
    }

    private suspend fun sendFinalStatus(
        decisionId: String,
        status: WearFinalStatusPayloadValue,
        clientId: String?
    ) {
        wearPhoneGateway.sendFinalStatus(decisionId, status)
        _finalStatus.tryEmit(IncomingUnlockFinalStatus(decisionId, clientId))
    }

    private data class PendingRequest(
        val clientId: String,
        val requestId: String?,
        val createdAt: Long = System.currentTimeMillis()
    )

    private sealed interface ResolveResult {
        data object AlreadyCompleted : ResolveResult
        data class Ready(val pendingRequest: PendingRequest) : ResolveResult
    }

    companion object {
        private const val MAX_COMPLETED_HISTORY = 100
        private const val REQUEST_TTL_MS = 300_000L // 5 minutes
    }
}
