package com.xxmrk888ytxx.portal.data.wear

import android.content.Context
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WearPhoneGatewayImpl @Inject constructor(
    context: Context,
    private val json: Json
) : WearPhoneGateway {

    private val messageClient = Wearable.getMessageClient(context)
    private val capabilityClient = Wearable.getCapabilityClient(context)

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
        fastDebugLog("Phone: Sending incoming unlock request to watch: device=$deviceName, decisionId=$decisionId")
        sendToAllReachableWatchNodes(
            WearDataLayerProtocol.INCOMING_REQUEST_PATH,
            json.encodeToString(payload).encodeToByteArray()
        )
    }

    override suspend fun sendFinalStatus(decisionId: String, status: WearFinalStatusPayloadValue) {
        val payload = WearFinalStatusPayload(decisionId, status)
        fastDebugLog("Phone: Sending final status to watch: decisionId=$decisionId, status=$status")
        sendToAllReachableWatchNodes(
            WearDataLayerProtocol.FINAL_STATUS_PATH,
            json.encodeToString(payload).encodeToByteArray()
        )
    }

    private suspend fun sendToAllReachableWatchNodes(path: String, data: ByteArray) {
        withContext(Dispatchers.IO) {
            runCatching {
                val capabilityInfo = capabilityClient.getCapability(
                    WearDataLayerProtocol.CAPABILITY_WATCH_APP,
                    CapabilityClient.FILTER_REACHABLE
                ).await()

                fastDebugLog("Phone: Found ${capabilityInfo.nodes.size} watch node(s) with capability ${WearDataLayerProtocol.CAPABILITY_WATCH_APP}: ${capabilityInfo.nodes.map { "${it.displayName}(${it.id})" }}")
                capabilityInfo.nodes.forEach { node ->
                    val messageId = messageClient.sendMessage(node.id, path, data).await()
                    fastDebugLog("Phone: Sent message on path $path to ${node.displayName} (${node.id}), messageId: $messageId")
                }
            }.onFailure {
                fastDebugLog("Phone: Error sending message on path $path: ${it.message}")
            }
        }
    }
}
