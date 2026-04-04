package com.xxmrk888ytxx.portal.data.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.service.model.ClientUnlockServiceParams
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import com.xxmrk888ytxx.portal.domain.WOLManager
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.WifiDevice
import com.xxmrk888ytxx.portal.utils.getParsableExtraCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

abstract class ClientUnlockService(
    private val wolManager: WOLManager,
    private val deviceUnlockManager: DeviceUnlockManager,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository
) : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    protected abstract val notificationId: Int
    protected abstract val notification: Notification
    protected abstract val action: String

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val clientUnlockServiceParams = intent?.getParsableExtraCompat(
            CLIENT_UNLOCK_SERVICE_PARAMS_KEY,
            ClientUnlockServiceParams::class.java
        )
        if (intent?.action != action || clientUnlockServiceParams == null) {
            fastDebugLog("${intent?.action} != $action or clientUnlockServiceParams $clientUnlockServiceParams == null")
            stopSelf(startId)
            return START_NOT_STICKY
        }
        startForeground(notificationId, notification)
        serviceScope.launch {
            handleUnlock(clientUnlockServiceParams)
        }.invokeOnCompletion { stopSelf(startId) }
        return START_NOT_STICKY
    }

    private suspend fun handleUnlock(clientUnlockServiceParams: ClientUnlockServiceParams) {
        val wifiDevice =
            wifiDeviceRepository.getDeviceById(clientUnlockServiceParams.clientId).first()
        val bluetoothDevice =
            bluetoothDeviceRepository.getDeviceById(clientUnlockServiceParams.clientId).first()


        when {
            wifiDevice != null -> doUnlockWifiDevice(wifiDevice, clientUnlockServiceParams)
            bluetoothDevice != null -> unlockBluetoothDevice(
                bluetoothDevice,
                clientUnlockServiceParams
            )

            else -> fastDebugLog("Device where id = ${clientUnlockServiceParams.clientId} doesn't exist. $this Skip ")
        }
    }

    private suspend fun unlockBluetoothDevice(
        bluetoothDevice: BluetoothDevice,
        clientUnlockServiceParams: ClientUnlockServiceParams
    ) {
        doUnlock(clientUnlockServiceParams) { tryBluetoothDeviceUnlock(bluetoothDevice, clientUnlockServiceParams) }
    }

    private suspend fun doUnlockWifiDevice(
        wifiDevice: WifiDevice,
        clientUnlockServiceParams: ClientUnlockServiceParams
    ) {
        val macAddress = wifiDevice.wolMacAddress
        if (macAddress == null) {
            fastDebugLog("Mac address is null. WOL unavailable for deviceId ${wifiDevice.clientId} ")
        }
        doUnlock(clientUnlockServiceParams) {
            tryWifiDeviceUnlock(
                wifiDevice = wifiDevice,
                macAddress = macAddress,
                clientUnlockServiceParams = clientUnlockServiceParams
            )
        }
    }

    private suspend fun doUnlock(
        clientUnlockServiceParams: ClientUnlockServiceParams,
        unlockBlock: suspend () -> Boolean
    ) {
        withTimeoutOrNull(WOL_UNLOCK_TIMEOUT_MILLS) {
            do {
                val isUnlockSuccessful = unlockBlock()
                if (isUnlockSuccessful) {
                    fastDebugLog("Unlock success. Stop $this")
                    return@withTimeoutOrNull
                } else {
                    fastDebugLog("Unlock failed. Restart $this")
                    delay(RETRY_UNLOCK_TIMEOUT)
                }
            } while (isActive && clientUnlockServiceParams.tryToRetryUnlockUntilSuccessOrTimeout)
        } ?: fastDebugLog("Unlock timeout. Stop $this")
    }

    private suspend fun tryWifiDeviceUnlock(
        wifiDevice: WifiDevice,
        macAddress: String?,
        clientUnlockServiceParams: ClientUnlockServiceParams
    ): Boolean {
        return try {
            if (macAddress != null && clientUnlockServiceParams.isSendWOLRequest) {
                wolManager.sendWOLRequest(macAddress)
            }
            if (clientUnlockServiceParams.isSendUnlockRequest) {
                return deviceUnlockManager.unlockWifiDevice(wifiDevice).isSuccess
            }
            true
        } catch (e: Exception) {
            fastDebugLog("Error during tryUnlock: ${e.message}")
            false
        }
    }

    private suspend fun tryBluetoothDeviceUnlock(
        bluetoothDevice: BluetoothDevice,
        clientUnlockServiceParams: ClientUnlockServiceParams
    ): Boolean {
        return try {
            if (clientUnlockServiceParams.isSendUnlockRequest) {
                deviceUnlockManager.unlockBluetoothDevice(bluetoothDevice).isSuccess
            }
            true
        } catch (e: Exception) {
            fastDebugLog("Error during tryUnlock: ${e.message}")
            false
        }
    }

    override fun onTimeout(startId: Int) {
        stopSelf(startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val CLIENT_UNLOCK_SERVICE_PARAMS_KEY = "CLIENT_UNLOCK_SERVICE_PARAMS_KEY"
        const val RETRY_UNLOCK_TIMEOUT = 2000L
        const val WOL_UNLOCK_TIMEOUT_MILLS = 170_000L
    }
}