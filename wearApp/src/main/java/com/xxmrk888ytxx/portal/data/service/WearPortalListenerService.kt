package com.xxmrk888ytxx.portal.data.service

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.WearDataLayerProtocol
import com.xxmrk888ytxx.portal.data.WearDeviceTransportPayload
import com.xxmrk888ytxx.portal.data.WearDevicesPayload
import com.xxmrk888ytxx.portal.data.WearFinalStatusPayload
import com.xxmrk888ytxx.portal.data.WearIncomingUnlockPayload
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.IncomingRequestPresenter
import com.xxmrk888ytxx.portal.domain.IncomingRequestRepository
import com.xxmrk888ytxx.portal.domain.WearNodeValidator
import com.xxmrk888ytxx.portal.domain.model.Device
import com.xxmrk888ytxx.portal.domain.model.DeviceTransport
import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Entry point for Data Layer events delivered to the watch.
 *
 * Handles device list sync, incoming unlock requests, and final request status. Business state is
 * delegated to repositories so UI can observe it independently from service lifetime.
 */
class WearPortalListenerService @Inject constructor(
    private val json: Json,
    private val deviceRepository: DeviceRepository,
    private val incomingRequestRepository: IncomingRequestRepository,
    private val incomingRequestPresenter: IncomingRequestPresenter,
    private val wearNodeValidator: WearNodeValidator,
    private val applicationScope: CoroutineScope
) : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        fastDebugLog("Watch: onDataChanged with ${dataEvents.count} events")
        dataEvents.forEach { event ->
            if (event.type != DataEvent.TYPE_CHANGED) return@forEach
            if (event.dataItem.uri.path != WearDataLayerProtocol.PROFILES_PATH) return@forEach

            val payload = DataMapItem.fromDataItem(event.dataItem)
                .dataMap
                .getString(WearDataLayerProtocol.PROFILE_PAYLOAD_KEY)
                ?: return@forEach

            fastDebugLog("Watch: Received device profiles snapshot from DataLayer: $payload")
            runCatching {
                val devicesPayload = json.decodeFromString<WearDevicesPayload>(payload)
                deviceRepository.updateDevices(
                    devicesPayload.devices.map { device ->
                        Device(
                            clientId = device.clientId,
                            name = device.name,
                            transport = when (device.transport) {
                                WearDeviceTransportPayload.WIFI -> DeviceTransport.WIFI
                                WearDeviceTransportPayload.BLUETOOTH -> DeviceTransport.BLUETOOTH
                            },
                            isWakeOnLanAvailable = device.isWakeOnLanAvailable
                        )
                    }
                )
                fastDebugLog("Watch: Updated ${devicesPayload.devices.size} devices in repository")
            }.onFailure {
                fastDebugLog("Watch: Failed to decode devices payload: ${it.message}")
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        fastDebugLog("Watch: onMessageReceived from ${messageEvent.sourceNodeId}, path: ${messageEvent.path}")
        applicationScope.launch {
            if (!wearNodeValidator.isTrustedPhoneNode(messageEvent.sourceNodeId)) {
                fastDebugLog("Watch: Ignored message on path ${messageEvent.path} from untrusted node ${messageEvent.sourceNodeId}")
                return@launch
            }

            val body = messageEvent.data.decodeToString()
            fastDebugLog("Watch: Processing message body: $body")
            runCatching {
                when (messageEvent.path) {
                    WearDataLayerProtocol.INCOMING_UNLOCK_REQUEST_PATH -> {
                        val payload = json.decodeFromString<WearIncomingUnlockPayload>(body)
                        fastDebugLog("Watch: Incoming unlock request for ${payload.deviceName} (${payload.clientId}, decisionId=${payload.decisionId})")
                        val request = IncomingUnlockRequest(
                            decisionId = payload.decisionId,
                            clientId = payload.clientId,
                            deviceName = payload.deviceName
                        )
                        incomingRequestRepository.put(request)
                        incomingRequestPresenter.present(request)
                    }

                    WearDataLayerProtocol.FINAL_STATUS_PATH -> {
                        val payload = json.decodeFromString<WearFinalStatusPayload>(body)
                        fastDebugLog("Watch: Final status received for decisionId=${payload.decisionId}: ${payload.status}")
                        incomingRequestRepository.markCompleted(payload.decisionId)
                        incomingRequestPresenter.cancel(payload.decisionId)
                    }
                }
            }.onFailure {
                fastDebugLog("Watch: Error processing message on path ${messageEvent.path}: ${it.message}")
            }
        }
    }
}
