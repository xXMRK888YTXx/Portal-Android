package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.portal.data.wear.WearDecisionPayloadValue
import com.xxmrk888ytxx.portal.data.wear.WearFinalStatusPayload
import com.xxmrk888ytxx.portal.data.wear.WearFinalStatusPayloadValue
import com.xxmrk888ytxx.portal.domain.IncomingUnlockDecisionCoordinator
import com.xxmrk888ytxx.portal.domain.UnlockMessageSender
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.UUID
import javax.inject.Inject

class IncomingUnlockDecisionCoordinatorImpl @Inject constructor(
    private val unlockMessageSender: UnlockMessageSender,
    private val wearPhoneGateway: WearPhoneGateway
) : IncomingUnlockDecisionCoordinator {

    private val pendingRequests = mutableMapOf<String, PendingRequest>()
    private val requestIdIndex = mutableMapOf<String, String>()
    private val completedDecisionIds = mutableSetOf<String>()

    private val _finalStatus = MutableSharedFlow<WearFinalStatusPayload>(extraBufferCapacity = 8)
    override val finalStatus: Flow<WearFinalStatusPayload> = _finalStatus

    @Synchronized
    override fun register(clientId: String, requestId: String?): String {
        val indexKey = requestId?.let { "$clientId:$it" }
        if (indexKey != null) {
            requestIdIndex[indexKey]?.let { return it }
        }

        val decisionId = UUID.randomUUID().toString()
        pendingRequests[decisionId] = PendingRequest(clientId, requestId)
        if (indexKey != null) {
            requestIdIndex[indexKey] = decisionId
        }
        return decisionId
    }

    @Synchronized
    override fun findDecisionId(clientId: String, requestId: String?): String? {
        if (requestId == null) return null
        return requestIdIndex["$clientId:$requestId"]
    }

    override suspend fun resolve(decisionId: String, decision: WearDecisionPayloadValue): Boolean {
        val pendingRequest = synchronized(this) {
            if (decisionId in completedDecisionIds) return false
            completedDecisionIds += decisionId
            pendingRequests.remove(decisionId)
        } ?: run {
            wearPhoneGateway.sendFinalStatus(
                decisionId,
                WearFinalStatusPayloadValue.ALREADY_COMPLETED
            )
            _finalStatus.tryEmit(
                WearFinalStatusPayload(decisionId, WearFinalStatusPayloadValue.ALREADY_COMPLETED)
            )
            return false
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
        wearPhoneGateway.sendFinalStatus(decisionId, status)
        _finalStatus.tryEmit(WearFinalStatusPayload(decisionId, status))
        return true
    }

    private data class PendingRequest(
        val clientId: String,
        val requestId: String?
    )
}
