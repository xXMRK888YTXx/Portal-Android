package com.xxmrk888ytxx.portal.data.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import com.xxmrk888ytxx.portal.domain.WOLManager
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.model.WifiDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class WOLUnlockService @Inject constructor(
    private val wolManager: WOLManager,
    private val deviceUnlockManager: DeviceUnlockManager,
    private val wifiDeviceRepository: WifiDeviceRepository,
) : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    val notification: Notification
        get() = buildNotification(WOL_UNLOCK_CHANNEL_ID) {
            setContentTitle(getString(R.string.unlocking_the_device_wol))
            setContentText(getString(R.string.please_wait_a_moment))
        }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val deviceId = intent?.getStringExtra(DEVICE_ID_EXTRA)
        if (intent?.action != WOL_UNLOCK_ACTION || deviceId == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }
        startForeground(NOTIFICATION_ID, notification)
        val isOneTimeMode = intent.getBooleanExtra(ONE_TIME_REQUEST_MODE_ID, true)
        serviceScope.launch {
            handleUnlock(deviceId, isOneTimeMode)
        }.invokeOnCompletion { stopSelf(startId) }
        return START_NOT_STICKY
    }

    private suspend fun handleUnlock(deviceId: String, isOneTimeMode: Boolean) {
        val wifiDevice =  wifiDeviceRepository.getDeviceById(deviceId).first()

        if (wifiDevice == null) {
            fastDebugLog("Device where id = $deviceId doesn't exist. Stop WOL Unlock")
            return
        }

        val macAddress = wifiDevice.wolMacAddress
        if (macAddress == null) {
            fastDebugLog("Device where id = $deviceId doesn't have mac address. Stop WOL Unlock")
            return
        }

        if (isOneTimeMode) {
            tryUnlock(wifiDevice, macAddress)
        } else {
            performRetryUnlock(wifiDevice, macAddress)
        }
    }

    private suspend fun performRetryUnlock(wifiDevice: WifiDevice, macAddress: String) {
        withTimeoutOrNull(WOL_UNLOCK_TIMEOUT_MILLS) {
            while (isActive) {
                val isUnlockSuccessful = tryUnlock(wifiDevice, macAddress)
                if (isUnlockSuccessful) {
                    fastDebugLog("Unlock success. Stop WOL Unlock")
                    return@withTimeoutOrNull true
                } else {
                    fastDebugLog("Unlock failed. Restart WOL Unlock")
                    delay(RETRY_UNLOCK_TIMEOUT)
                }
            }
        } ?: fastDebugLog("Unlock timeout. Stop WOL Unlock")
    }

    private suspend fun tryUnlock(wifiDevice: WifiDevice, macAddress: String): Boolean {
        return try {
            wolManager.sendWOLRequest(macAddress)
            deviceUnlockManager.unlockWifiDevice(wifiDevice).isSuccess
        } catch (e: Exception) {
            fastDebugLog("Error during tryUnlock: ${e.message}")
            false
        }
    }

    override fun onCreate() {
        super.onCreate()
        buildNotificationChannel(
            WOL_UNLOCK_CHANNEL_ID,
            getString(R.string.unblocking_via_wol)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onTimeout(startId: Int) {
        stopSelf()
    }

    companion object {
        const val NOTIFICATION_ID = 5553
        const val ONE_TIME_REQUEST_MODE_ID = "ONE_TIME_REQUEST_MODE_ID"
        const val WOL_UNLOCK_TIMEOUT_MILLS = 170_000L
        const val RETRY_UNLOCK_TIMEOUT = 2000L
        const val DEVICE_ID_EXTRA = "DEVICE_ID_EXTRA"
        const val WOL_UNLOCK_CHANNEL_ID = "WOL_UNLOCK_CHANNEL_ID"
        const val WOL_UNLOCK_ACTION = "com.xxmrk888ytxx.portal.WOL_UNLOCK_ACTION"
    }
}