package com.xxmrk888ytxx.portal.data

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Wearable MessageClient implementation of [WearPhoneGateway].
 *
 * Commands are sent to the verified companion phone node. Commands are not queued: if the phone is not
 * connected, the caller receives an error immediately.
 */
class WearPhoneGatewayImpl @Inject constructor(
    context: Context,
    private val json: Json
) : WearPhoneGateway {

    private val capabilityClient = Wearable.getCapabilityClient(context)
    private val messageClient = Wearable.getMessageClient(context)

    override suspend fun sendUnlockCommand(clientId: String) {
        fastDebugLog("Watch: Sending unlock command for clientId: $clientId")
        send(
            WearDataLayerProtocol.UNLOCK_COMMAND_PATH,
            json.encodeToString(WearUnlockCommandPayload(clientId)).encodeToByteArray()
        )
    }

    override suspend fun sendWakeOnLanUnlockCommand(clientId: String) {
        fastDebugLog("Watch: Sending WOL unlock command for clientId: $clientId")
        send(
            WearDataLayerProtocol.WOL_UNLOCK_COMMAND_PATH,
            json.encodeToString(WearUnlockCommandPayload(clientId)).encodeToByteArray()
        )
    }

    override suspend fun sendDecision(decisionId: String, decision: WearDecisionPayloadValue) {
        fastDebugLog("Watch: Sending decision $decision for decisionId: $decisionId")
        send(
            WearDataLayerProtocol.UNLOCK_REQUEST_DECISION_PATH,
            json.encodeToString(WearDecisionPayload(decisionId, decision)).encodeToByteArray()
        )
    }

    override suspend fun requestDeviceSync() {
        fastDebugLog("Watch: Requesting device sync from phone")
        send(WearDataLayerProtocol.SYNC_DEVICES_REQUEST_PATH, ByteArray(0))
    }

    override suspend fun isPhoneAvailable(): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val capabilityInfo = Tasks.await(
                capabilityClient.getCapability(
                    WearDataLayerProtocol.CAPABILITY_PHONE_APP,
                    CapabilityClient.FILTER_REACHABLE
                )
            )
            val available = capabilityInfo.nodes.isNotEmpty()
            fastDebugLog("Watch: isPhoneAvailable: $available (nodes: ${capabilityInfo.nodes.map { it.displayName }})")
            available
        }.getOrDefault(false)
    }

    private suspend fun send(path: String, data: ByteArray) = withContext(Dispatchers.IO) {
        fastDebugLog("Watch: Finding target phone node for path: $path...")
        val capabilityInfo = Tasks.await(
            capabilityClient.getCapability(
                WearDataLayerProtocol.CAPABILITY_PHONE_APP,
                CapabilityClient.FILTER_REACHABLE
            )
        )

        val phoneNode = capabilityInfo.nodes.firstOrNull { it.isNearby }
            ?: capabilityInfo.nodes.firstOrNull()
            ?: run {
                fastDebugLog("Watch: ERROR - No reachable phone node with capability ${WearDataLayerProtocol.CAPABILITY_PHONE_APP} found to send path: $path")
                error("Phone is unavailable")
            }

        fastDebugLog("Watch: Sending message to phone node: ${phoneNode.displayName} (${phoneNode.id}) on path: $path (${data.size} bytes)")
        val messageId = Tasks.await(messageClient.sendMessage(phoneNode.id, path, data))
        fastDebugLog("Watch: Successfully sent message to ${phoneNode.id} on path: $path (messageId: $messageId)")
    }
}
