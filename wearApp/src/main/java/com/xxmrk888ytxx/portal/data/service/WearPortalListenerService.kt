package com.xxmrk888ytxx.portal.data.service

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.xxmrk888ytxx.portal.data.WearDataLayerProtocol
import com.xxmrk888ytxx.portal.data.WearDeviceTransportPayload
import com.xxmrk888ytxx.portal.data.WearDevicesPayload
import com.xxmrk888ytxx.portal.data.WearFinalStatusPayload
import com.xxmrk888ytxx.portal.data.WearIncomingUnlockPayload
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.IncomingRequestPresenter
import com.xxmrk888ytxx.portal.domain.IncomingRequestRepository
import com.xxmrk888ytxx.portal.domain.model.Device
import com.xxmrk888ytxx.portal.domain.model.DeviceTransport
import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WearPortalListenerService @Inject constructor(
    private val json: Json,
    private val deviceRepository: DeviceRepository,
    private val incomingRequestRepository: IncomingRequestRepository,
    private val incomingRequestPresenter: IncomingRequestPresenter
) : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type != DataEvent.TYPE_CHANGED) return@forEach
            if (event.dataItem.uri.path != WearDataLayerProtocol.PROFILES_PATH) return@forEach

            val payload = DataMapItem.fromDataItem(event.dataItem)
                .dataMap
                .getString(WearDataLayerProtocol.PROFILE_PAYLOAD_KEY)
                ?: return@forEach

            deviceRepository.updateDevices(
                json.decodeFromString<WearDevicesPayload>(payload).devices.map { device ->
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
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val body = messageEvent.data.decodeToString()
        when (messageEvent.path) {
            WearDataLayerProtocol.INCOMING_REQUEST_PATH -> {
                val payload = json.decodeFromString<WearIncomingUnlockPayload>(body)
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
                incomingRequestRepository.markCompleted(payload.decisionId)
                incomingRequestPresenter.cancel(payload.decisionId)
            }
        }
    }
}
