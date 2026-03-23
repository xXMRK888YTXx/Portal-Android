package com.xxmrk888ytxx.portal.data.unlockService

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.saveCall
import com.xxmrk888ytxx.portal.data.model.BluetoothRemoteUnlockMessage
import com.xxmrk888ytxx.portal.data.model.BluetoothRemoteUnlockRequest
import com.xxmrk888ytxx.portal.data.model.WifiRemoteUnlockMessage
import com.xxmrk888ytxx.portal.data.model.WifiRemoteUnlockMessage.ApproveUnlockWifi
import com.xxmrk888ytxx.portal.data.model.WifiRemoteUnlockMessage.RejectUnlockWifi
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import com.xxmrk888ytxx.portal.domain.model.BluetoothConnection
import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.unlockservice.core.NetworkDriver
import com.xxmrk888ytxx.unlockservice.core.UnlockMessage
import com.xxmrk888ytxx.unlockservice.core.UnlockRequest
import com.xxmrk888ytxx.unlockservice.exception.DeviceNotPairedException
import com.xxmrk888ytxx.unlockservice.exception.InvalidClientIdException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.text.Charsets.UTF_8

class BluetoothDriver @Inject constructor(
    private val bluetoothManager: BluetoothManager,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository,
    private val json: Json
) : NetworkDriver {

    @Serializable
    private data class RegisterModel(
        val type: String = "register"
    )


    override suspend fun connect(
        clientId: String,
        messagesForSendChannel: Channel<UnlockMessage>,
        receivedRequestChannel: Channel<UnlockRequest>
    ) {
        val device = bluetoothDeviceRepository.getDeviceById(clientId).first()
            ?: throw InvalidClientIdException(clientId)
        lateinit var bluetoothConnection: BluetoothConnection
        try {
            bluetoothConnection = bluetoothManager.openConnection(device.macAddress)
            val registerModel = RegisterModel()
            val registerJsonString = Json.encodeToString(registerModel)
            bluetoothConnection.sendData(registerJsonString.toByteArray(UTF_8))
            fastDebugLog("Sent register message")
            observeMessages(
                bluetoothConnection = bluetoothConnection,
                bluetoothDevice = device,
                messagesForSendChannel = messagesForSendChannel,
                receivedRequestChannel = receivedRequestChannel,
                clientId = clientId
            )
        } catch (_: IllegalArgumentException) {
            throw DeviceNotPairedException(device.macAddress)
        } finally {
            withContext(NonCancellable) {
                saveCall(isPrintToDebug = false) { bluetoothConnection.release() }
            }
        }
    }

    private suspend fun observeMessages(
        bluetoothConnection: BluetoothConnection,
        bluetoothDevice: BluetoothDevice,
        messagesForSendChannel: Channel<UnlockMessage>,
        receivedRequestChannel: Channel<UnlockRequest>,
        clientId: String
    ) = coroutineScope {
        val sendJob = launch {

            // Send
            for (messageForSend in messagesForSendChannel) {
                try {
                    val jsonString = when (messageForSend) {
                        is UnlockMessage.ApproveUnlock -> json.encodeToString(
                            BluetoothRemoteUnlockMessage.ApproveUnlockBluetooth(
                                clientId = bluetoothDevice.clientId,
                                requestId = messageForSend.requestId,
                            )
                        )

                        is UnlockMessage.Canceled -> json.encodeToString(
                            BluetoothRemoteUnlockMessage.RejectUnlockBluetooth(
                                clientId = bluetoothDevice.clientId,
                                requestId = messageForSend.requestId
                            )
                        )
                    }
                    fastDebugLog("Try to send message: $messageForSend")
                    bluetoothConnection.sendData(jsonString.toByteArray(UTF_8))
                    fastDebugLog("Sent message: $messageForSend")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    fastDebugLog("Error while sending message: $messageForSend. Exception ${e.message}")
                    break
                }
            }
        }

        // Read
        val readJob = launch {
            bluetoothConnection.incomingData.collect { data ->
                val jsonString = data.toString(UTF_8)
                fastDebugLog("Received message: $jsonString")
                val request = jsonString.remoteUnlockRequest
                if (request?.clientId != clientId) return@collect
                val domainRequest = when (request.type) {
                    BluetoothRemoteUnlockRequest.UNLOCK_REQUEST_TYPE -> UnlockRequest.Auth(request.requestId)
                    else -> null
                }
                if (domainRequest != null) {
                    receivedRequestChannel.send(domainRequest)
                } else {
                    fastDebugLog("Unknown message type: ${request?.type}")
                }
            }
        }
        bluetoothConnection.isClosed.first { it }
        sendJob.cancel()
        readJob.cancel()
    }

    private val String.remoteUnlockRequest: BluetoothRemoteUnlockRequest?
        get() = try {
            json.decodeFromString<BluetoothRemoteUnlockRequest>(this)
        } catch (e: Exception) {
            fastDebugLog("Error while parsing to RemoteUnlockRequest: exception: $e, string: $this")
            null
        }
}