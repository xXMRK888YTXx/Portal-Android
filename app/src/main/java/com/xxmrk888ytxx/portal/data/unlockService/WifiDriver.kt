package com.xxmrk888ytxx.portal.data.unlockService

import android.R.id.message
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.KtorFactory
import com.xxmrk888ytxx.portal.data.model.RemoteUnlockMessage.ApproveUnlock
import com.xxmrk888ytxx.portal.data.model.RemoteUnlockMessage.RejectUnlock
import com.xxmrk888ytxx.portal.data.model.RemoteUnlockRequest
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.MdnsManager
import com.xxmrk888ytxx.portal.utils.waitHostForClient
import com.xxmrk888ytxx.unlockservice.core.UnlockMessage
import com.xxmrk888ytxx.unlockservice.core.UnlockRequest
import com.xxmrk888ytxx.unlockservice.exception.InvalidClientIdException
import com.xxmrk888ytxx.unlockservice.core.NetworkDriver
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class WifiDriver @Inject constructor(
    private val ktorFactory: KtorFactory,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val mdnsManager: MdnsManager,
    private val deviceSettingsRepository: DeviceSettingsRepository,
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

        val client = ktorFactory.createUnlockClient(
            device.clientCertificate,
            device.serverCertificateFingerprint
        )
        val host = when {
            deviceSettings.searchIpDynamically -> mdnsManager.waitHostForClient(clientId)
                .also { fastDebugLog("In wifiDriver mdns found host: $it") }
                ?: device.host.also { fastDebugLog("In wifiDriver mdns not found host. Using default") }

            else -> device.host
        }
        val urlString = "wss://$host:29170/ws"
        fastDebugLog("Try to openConnection to websocket server host: $urlString")
        client.webSocket(urlString = urlString) {
            fastDebugLog("Connected to websocket server")

            //Send coroutine
            launch {
                for (messageForSend in messagesForSendChannel) {
                    try {
                        val remoteMessage = when (messageForSend) {
                            is UnlockMessage.ApproveUnlock -> ApproveUnlock(
                                clientId = clientId,
                                requestId = messageForSend.requestId
                            )

                            is UnlockMessage.Canceled -> RejectUnlock(
                                clientId = clientId,
                                requestId =  messageForSend.requestId
                            )
                        }
                        fastDebugLog("Try to send message: $messageForSend")
                        sendSerialized(remoteMessage)
                        fastDebugLog("Sent message: $messageForSend")
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        fastDebugLog("Error while sending message: $message. Exception ${e.message}")
                        break
                    }
                }
            }

            //Read loop
            while (currentCoroutineContext().isActive) {
                fastDebugLog("Waiting messages")
                val response = receiveDeserialized<RemoteUnlockRequest>()
                val localRequest = when (response.type) {
                    UNLOCK_REQUEST_TYPE -> UnlockRequest.Auth(response.requestId)
                    else -> null
                }
                fastDebugLog("Received message: $response")
                if (localRequest != null) {
                    receivedRequestChannel.send(localRequest)
                } else {
                    fastDebugLog("Unknown message type: ${response.type}")
                }
            }
        }
    }

    companion object {
        const val UNLOCK_REQUEST_TYPE = "unlock_request"
    }


}