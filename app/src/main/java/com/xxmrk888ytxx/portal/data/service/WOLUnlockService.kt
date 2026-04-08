package com.xxmrk888ytxx.portal.data.service

import android.app.Notification
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Intent
import android.os.IBinder
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import com.xxmrk888ytxx.portal.domain.WOLManager
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

class WOLUnlockService @Inject constructor(
    wifiDeviceRepository: WifiDeviceRepository,
    deviceUnlockManager: DeviceUnlockManager,
    bluetoothDeviceRepository: BluetoothDeviceRepository,
    wolManager: WOLManager
) : ClientUnlockService(
    wolManager,
    deviceUnlockManager,
    wifiDeviceRepository,
    bluetoothDeviceRepository
) {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val notificationId: Int
        get() = NOTIFICATION_ID
    override val action: String
        get() = WOL_UNLOCK_ACTION
    override val notification: Notification
        get() = buildNotification(WOL_UNLOCK_CHANNEL_ID) {
            setContentTitle(getString(R.string.unlocking_the_device_wol))
            setSmallIcon(com.xxmrk888ytxx.unlockservice.R.drawable.portal)
            setContentText(getString(R.string.please_wait_a_moment))
        }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        buildNotificationChannel(
            WOL_UNLOCK_CHANNEL_ID,
            getString(R.string.unblocking_via_wol)
        ) {
            importance = IMPORTANCE_LOW
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        private const val NOTIFICATION_ID = 5553
        const val WOL_UNLOCK_CHANNEL_ID = "WOL_UNLOCK_CHANNEL_ID"
        const val WOL_UNLOCK_ACTION = "com.xxmrk888ytxx.portal.WOL_UNLOCK_ACTION"
    }
}