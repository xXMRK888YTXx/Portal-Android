package com.xxmrk888ytxx.portal.data.wear

import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import com.xxmrk888ytxx.portal.domain.IncomingUnlockDecisionCoordinator
import com.xxmrk888ytxx.portal.domain.WOLServiceManager
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
    private val decisionCoordinator: IncomingUnlockDecisionCoordinator
) {
    suspend fun handleMessage(path: String, data: ByteArray) {
        val body = data.decodeToString()
        when (path) {
            WearDataLayerProtocol.UNLOCK_COMMAND_PATH -> {
                val command = json.decodeFromString<WearUnlockCommandPayload>(body)
                unlock(command.clientId)
            }

            WearDataLayerProtocol.WOL_UNLOCK_COMMAND_PATH -> {
                val command = json.decodeFromString<WearUnlockCommandPayload>(body)
                wolServiceManager.startWOLUnlock(command.clientId, trySendUnlockRequests = true)
            }

            WearDataLayerProtocol.DECISION_PATH -> {
                val decision = json.decodeFromString<WearDecisionPayload>(body)
                decisionCoordinator.resolve(decision.decisionId, decision.decision)
            }
        }
    }

    private suspend fun unlock(clientId: String) {
        val wifiDevice = wifiDeviceRepository.getDeviceById(clientId).first()
        if (wifiDevice != null) {
            deviceUnlockManager.unlockWifiDevice(wifiDevice).getOrThrow()
            return
        }

        val bluetoothDevice = bluetoothDeviceRepository.getDeviceById(clientId).first()
            ?: error("Device with clientId = $clientId not found")
        deviceUnlockManager.unlockBluetoothDevice(bluetoothDevice).getOrThrow()
    }
}
