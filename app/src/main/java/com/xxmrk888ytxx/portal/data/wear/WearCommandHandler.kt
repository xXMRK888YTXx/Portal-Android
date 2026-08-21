package com.xxmrk888ytxx.portal.data.wear

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import com.xxmrk888ytxx.portal.domain.IncomingUnlockDecisionCoordinator
import com.xxmrk888ytxx.portal.domain.WOLServiceManager
import com.xxmrk888ytxx.portal.domain.WearDeviceSyncManager
import com.xxmrk888ytxx.portal.domain.WearNodeValidator
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WearCommandHandler @Inject constructor(
    private val json: Json,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository,
    private val deviceUnlockManager: DeviceUnlockManager,
    private val wolServiceManager: WOLServiceManager,
    private val decisionCoordinator: IncomingUnlockDecisionCoordinator,
    private val wearDeviceSyncManager: WearDeviceSyncManager,
    private val wearNodeValidator: WearNodeValidator
) {
    suspend fun handleMessage(sourceNodeId: String, path: String, data: ByteArray) {
        fastDebugLog("Phone: Handling Wear message on path $path from sourceNodeId $sourceNodeId")
        if (!wearNodeValidator.isTrustedWatchNode(sourceNodeId)) {
            fastDebugLog("Phone: Rejected Wear message from untrusted node $sourceNodeId on path $path")
            return
        }

        val body = data.decodeToString()
        fastDebugLog("Phone: Decoding message on path $path: $body")
        runCatching {
            when (path) {
                WearDataLayerProtocol.UNLOCK_COMMAND_PATH -> {
                    val command = json.decodeFromString<WearUnlockCommandPayload>(body)
                    fastDebugLog("Phone: Executing UNLOCK_COMMAND for clientId: ${command.clientId}")
                    unlock(command.clientId)
                }

                WearDataLayerProtocol.WOL_UNLOCK_COMMAND_PATH -> {
                    val command = json.decodeFromString<WearUnlockCommandPayload>(body)
                    fastDebugLog("Phone: Executing WOL_UNLOCK_COMMAND for clientId: ${command.clientId}")
                    wolServiceManager.startWOLUnlock(command.clientId, trySendUnlockRequests = true)
                }

                WearDataLayerProtocol.DECISION_PATH -> {
                    val decision = json.decodeFromString<WearDecisionPayload>(body)
                    fastDebugLog("Phone: Resolving DECISION ${decision.decision} for decisionId: ${decision.decisionId}")
                    val resolved =
                        decisionCoordinator.resolve(decision.decisionId, decision.decision)
                    fastDebugLog("Phone: Decision resolved result: $resolved")
                }

                WearDataLayerProtocol.SYNC_DEVICES_REQUEST_PATH -> {
                    fastDebugLog("Phone: Received SYNC_DEVICES_REQUEST, triggering syncNow()")
                    wearDeviceSyncManager.syncNow()
                }
            }
        }.onFailure {
            fastDebugLog("Phone: Failed to handle Wear message on path $path: ${it.message}")
        }
    }

    private suspend fun unlock(clientId: String) {
        fastDebugLog("Phone: Looking up device with clientId: $clientId")
        val wifiDevice = wifiDeviceRepository.getDeviceById(clientId).first()
        if (wifiDevice != null) {
            fastDebugLog("Phone: Unlocking Wi-Fi device: ${wifiDevice.deviceName} ($clientId)...")
            val result = deviceUnlockManager.unlockWifiDevice(wifiDevice)
            fastDebugLog("Phone: Wi-Fi unlock result for $clientId: $result")
            return
        }

        val bluetoothDevice = bluetoothDeviceRepository.getDeviceById(clientId).first()
        if (bluetoothDevice != null) {
            fastDebugLog("Phone: Unlocking Bluetooth device: ${bluetoothDevice.name} ($clientId)...")
            val result = deviceUnlockManager.unlockBluetoothDevice(bluetoothDevice)
            fastDebugLog("Phone: Bluetooth unlock result for $clientId: $result")
        } else {
            fastDebugLog("Phone: Device with clientId = $clientId not found in either Wi-Fi or Bluetooth repository")
        }
    }
}
