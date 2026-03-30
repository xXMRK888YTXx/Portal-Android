package com.xxmrk888ytxx.portal.data.unlockService

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.NetworkFactory
import com.xxmrk888ytxx.portal.data.model.WebSocketEvent
import com.xxmrk888ytxx.portal.data.model.WifiRemoteUnlockMessage
import com.xxmrk888ytxx.portal.data.model.WifiRemoteUnlockRequest
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.MdnsManager
import com.xxmrk888ytxx.portal.utils.waitHostForClient
import com.xxmrk888ytxx.unlockservice.core.UnlockMessage
import com.xxmrk888ytxx.unlockservice.core.UnlockRequest
import com.xxmrk888ytxx.unlockservice.exception.InvalidClientIdException
import com.xxmrk888ytxx.unlockservice.core.NetworkDriver
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.text.Charsets.UTF_8

class WifiDriver @Inject constructor(
    private val networkFactory: NetworkFactory,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val mdnsManager: MdnsManager,
    private val deviceSettingsRepository: DeviceSettingsRepository,
    private val json: Json
) : NetworkDriver {
    override suspend fun connect(
        clientId: String,
        messagesForSendChannel: Channel<UnlockMessage>,
        receivedRequestChannel: Channel<UnlockRequest>
    ) {
        val device =
            wifiDeviceRepository.getDeviceById(clientId).first() ?: throw InvalidClientIdException(
                clientId
            )
        val deviceSettings =
            deviceSettingsRepository.getDeviceSettingsByDeviceId(deviceId = clientId).first()
                ?: throw InvalidClientIdException(clientId)
        val host = when {
            deviceSettings.searchIpDynamically -> mdnsManager.waitHostForClient(clientId)
                .also { fastDebugLog("In wifiDriver mdns found host: $it") }
                ?: device.host.also { fastDebugLog("In wifiDriver mdns not found host. Using default") }

            else -> device.host
        }
        val urlString = "wss://$host:29170/ws"
        fastDebugLog("Try to openConnection to websocket server host: $urlString")
        val connection = networkFactory.openWebSocketConnection(
            url = urlString,
            certificate = device.clientCertificate,
            trustedServerHashFingerprint = device.serverCertificateFingerprint
        )

        coroutineScope {
            withTimeoutOrNull(CONNECTION_TIMEOUT) {
                connection.isConnected.first { it }
            } ?: throw IOException("Failed connect to websocket server on $urlString")

            val sendJob = launch {
                for (messageForSend in messagesForSendChannel) {
                    try {
                        val jsonString = when (messageForSend) {
                            is UnlockMessage.ApproveUnlock -> json.encodeToString(WifiRemoteUnlockMessage.ApproveUnlockWifi(
                                clientId = clientId,
                                requestId = messageForSend.requestId
                            ))

                            is UnlockMessage.Canceled -> json.encodeToString(
                                WifiRemoteUnlockMessage.RejectUnlockWifi(
                                    clientId = clientId,
                                    requestId = messageForSend.requestId
                                )
                            )
                        }
                        fastDebugLog("Try to send message: $messageForSend")
                        connection.send(jsonString)
                        fastDebugLog("Sent message: $messageForSend")
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        fastDebugLog("Error while sending message: $messageForSend. Exception ${e.message}")
                        break
                    }
                }
            }
            val readJob = launch {
                fastDebugLog("Waiting messages")
                connection.eventFlow.collect { event ->
                    fastDebugLog("Received message: $event")
                    val request = when(event) {
                        is WebSocketEvent.BinaryMessage -> event.bytes.toByteArray().toString(UTF_8).wifiRemoteUnlockRequest
                        is WebSocketEvent.TextMessage -> event.text.wifiRemoteUnlockRequest
                        else -> null
                    }
                    val domainRequest = when (request?.type) {
                        WifiRemoteUnlockRequest.UNLOCK_REQUEST_TYPE -> UnlockRequest.Auth(request.requestId)
                        else -> null
                    }

                    if (domainRequest != null) {
                        receivedRequestChannel.send(domainRequest)
                    } else {
                        fastDebugLog("Unknown message type: ${request?.type}")
                    }

                    fastDebugLog("Waiting messages")
                }
            }
            fastDebugLog("Awaiting isClosed")
            connection.isClosed.first { it }
            sendJob.cancel()
            readJob.cancel()
        }
    }

    private val String.wifiRemoteUnlockRequest: WifiRemoteUnlockRequest?
        get() = try {
            json.decodeFromString<WifiRemoteUnlockRequest>(this)
        } catch (e: Exception) {
            fastDebugLog("Error while parsing to WifiRemoteUnlockRequest: exception: $e, string: $this")
            null
        }

    companion object {
        const val CONNECTION_TIMEOUT = 3000L
    }
}