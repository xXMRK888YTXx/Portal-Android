package com.xxmrk888ytxx.portal.data.wear

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WearPhoneGatewayImpl @Inject constructor(
    context: Context,
    private val json: Json
) : WearPhoneGateway {

    private val messageClient = Wearable.getMessageClient(context)
    private val nodeClient = Wearable.getNodeClient(context)

    override suspend fun sendIncomingUnlockRequest(
        decisionId: String,
        clientId: String,
        deviceName: String
    ) {
        val payload = WearIncomingUnlockPayload(
            decisionId = decisionId,
            clientId = clientId,
            deviceName = deviceName
        )
        sendToAllReachableNodes(
            WearDataLayerProtocol.INCOMING_REQUEST_PATH,
            json.encodeToString(payload).encodeToByteArray()
        )
    }

    override suspend fun sendFinalStatus(decisionId: String, status: WearFinalStatusPayloadValue) {
        val payload = WearFinalStatusPayload(decisionId, status)
        sendToAllReachableNodes(
            WearDataLayerProtocol.FINAL_STATUS_PATH,
            json.encodeToString(payload).encodeToByteArray()
        )
    }

    private suspend fun sendToAllReachableNodes(path: String, data: ByteArray) {
        withContext(Dispatchers.IO) {
            Tasks.await(nodeClient.connectedNodes).forEach { node ->
                Tasks.await(messageClient.sendMessage(node.id, path, data))
            }
        }
    }
}
