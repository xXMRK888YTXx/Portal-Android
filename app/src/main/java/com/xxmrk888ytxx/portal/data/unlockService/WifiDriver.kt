package com.xxmrk888ytxx.portal.data.unlockService

import android.R.id.message
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.KtorFactory
import com.xxmrk888ytxx.portal.data.model.RemoteUnlockMessage
import com.xxmrk888ytxx.portal.data.model.RemoteUnlockRequest
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.unlockservice.core.UnlockMessage
import com.xxmrk888ytxx.unlockservice.core.UnlockRequest
import com.xxmrk888ytxx.unlockservice.exception.InvalidClientIdException
import com.xxmrk888ytxx.unlockservice.wifiService.NetworkDriver
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WifiDriver @Inject constructor(
    private val ktorFactory: KtorFactory,
    private val deviceRepository: DeviceRepository
) : NetworkDriver {
    override suspend fun connect(
        clientId: String,
        messagesForSendChannel: Channel<UnlockMessage>,
        receivedRequestChannel: Channel<UnlockRequest>
    ) {
        val device =
            deviceRepository.getDeviceById(clientId).first() ?: throw InvalidClientIdException(
                clientId
            )
        val client = ktorFactory.createUnlockClient(
            device.clientCertificate,
            device.serverCertificateFingerprint
        )
        val host = "wss://${device.host}:29170/ws"
        fastDebugLog("Try to connect to websocket server host: $host")
        client.webSocket(urlString = host) {
            fastDebugLog("Connected to websocket server")

            //Send coroutine
            launch {
                for (messageForSend in messagesForSendChannel) {
                    try {
                        val remoteMessage = when(messageForSend) {
                            UnlockMessage.ApproveUnlock -> RemoteUnlockMessage.ApproveUnlock(
                                clientId = clientId
                            )
                        }
                        fastDebugLog("Try to send message: $messageForSend")
                        sendSerialized(remoteMessage)
                        fastDebugLog("Sent message: $messageForSend")
                    } catch (e: CancellationException) { throw e }
                    catch (e: Exception) {
                        fastDebugLog("Error while sending message: $message. Exception ${e.message}")
                        break
                    }
                }
            }

            //Read loop
            while (currentCoroutineContext().isActive) {
                fastDebugLog("Waiting messages")
                val response = receiveDeserialized<RemoteUnlockRequest>()
                val localRequest = when(response.type) {
                    UNLOCK_REQUEST_TYPE -> UnlockRequest.Auth
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