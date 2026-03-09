package com.xxmrk888ytxx.portal.data.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class UnlockFromShortcutService @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val deviceUnlockManager: DeviceUnlockManager,
) : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    val notification: Notification
        get() = buildNotification(SHORTCUT_UNLOCK_SERVICE_CHANNEL) {
            setContentTitle(getString(R.string.unlocking_the_device))
            setContentText(getString(R.string.please_wait_a_moment))
        }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        buildNotificationChannel(
            SHORTCUT_UNLOCK_SERVICE_CHANNEL,
            getString(R.string.notification_during_unlocking_via_shortcut)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val deviceId = intent?.getStringExtra(DEVICE_ID_EXTRA)
        if (intent?.action != SHORTCUT_UNLOCK_ACTION || deviceId == null) {
            stopSelf()
            return START_NOT_STICKY
        }
        startForeground(NOTIFICATION_ID, notification)
        doUnlock(deviceId,startId)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun doUnlock(deviceId: String, startId: Int) = serviceScope.launch {
        try {
            fastDebugLog("doUnlock: $deviceId")
            val device = deviceRepository.getDeviceById(deviceId).first().also { fastDebugLog("Device where id = $deviceId doesn't exits") } ?: return@launch
            deviceUnlockManager.unlockWifiDevice(device).getOrThrow()
        }catch (e: Exception) {
            fastDebugLog("Exception in UnlockFromShortcutService: $e")
        }
    }.invokeOnCompletion { stopSelf(startId) }


    companion object {
        const val SHORTCUT_UNLOCK_SERVICE_CHANNEL = "SHORTCUT_UNLOCK_SERVICE"
        const val NOTIFICATION_ID = 4653
        const val SHORTCUT_UNLOCK_ACTION = "com.xxmrk888ytxx.portal.SHORTCUT_UNLOCK_ACTION"
        const val DEVICE_ID_EXTRA = "DEVICE_ID_EXTRA"
    }
}