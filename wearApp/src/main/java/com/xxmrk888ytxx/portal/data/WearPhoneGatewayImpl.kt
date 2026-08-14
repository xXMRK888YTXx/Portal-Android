package com.xxmrk888ytxx.portal.data

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Wearable MessageClient implementation of [WearPhoneGateway].
 *
 * Commands are sent to the first connected phone node. Commands are not queued: if the phone is not
 * connected, the caller receives an error immediately.
 */
class WearPhoneGatewayImpl @Inject constructor(
    context: Context,
    private val json: Json
) : WearPhoneGateway {

    private val nodeClient = Wearable.getNodeClient(context)
    private val messageClient = Wearable.getMessageClient(context)

    override suspend fun sendUnlockCommand(clientId: String) {
        send(
            WearDataLayerProtocol.UNLOCK_COMMAND_PATH,
            json.encodeToString(WearUnlockCommandPayload(clientId)).encodeToByteArray()
        )
    }

    override suspend fun sendWakeOnLanUnlockCommand(clientId: String) {
        send(
            WearDataLayerProtocol.WOL_UNLOCK_COMMAND_PATH,
            json.encodeToString(WearUnlockCommandPayload(clientId)).encodeToByteArray()
        )
    }

    override suspend fun sendDecision(decisionId: String, decision: WearDecisionPayloadValue) {
        send(
            WearDataLayerProtocol.UNLOCK_REQUEST_DECISION_PATH,
            json.encodeToString(WearDecisionPayload(decisionId, decision)).encodeToByteArray()
        )
    }

    override suspend fun requestDeviceSync() {
        send(WearDataLayerProtocol.SYNC_DEVICES_REQUEST_PATH, ByteArray(0))
    }

    override suspend fun isPhoneAvailable(): Boolean = withContext(Dispatchers.IO) {
        Tasks.await(nodeClient.connectedNodes).isNotEmpty()
    }

    private suspend fun send(path: String, data: ByteArray) = withContext(Dispatchers.IO) {
        val nodes = Tasks.await(nodeClient.connectedNodes)
        val phoneNode = nodes.firstOrNull() ?: error("Phone is unavailable")
        Tasks.await(messageClient.sendMessage(phoneNode.id, path, data))
    }
}
