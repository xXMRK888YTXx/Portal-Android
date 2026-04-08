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
import javax.inject.Inject

class UnlockFromShortcutService @Inject constructor(
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
    override val notificationId: Int
        get() = NOTIFICATION_ID

    override val action: String
        get() = SHORTCUT_UNLOCK_ACTION

    override val notification: Notification
        get() = buildNotification(SHORTCUT_UNLOCK_SERVICE_CHANNEL) {
            setContentTitle(getString(R.string.unlocking_the_device))
            setSmallIcon(com.xxmrk888ytxx.unlockservice.R.drawable.portal)
            setContentText(getString(R.string.please_wait_a_moment))
        }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        buildNotificationChannel(
            SHORTCUT_UNLOCK_SERVICE_CHANNEL,
            getString(R.string.notification_during_unlocking_via_shortcut)
        ) {
            importance = IMPORTANCE_LOW
        }
    }


    companion object {
        const val SHORTCUT_UNLOCK_SERVICE_CHANNEL = "SHORTCUT_UNLOCK_SERVICE"
        private const val NOTIFICATION_ID = 4653
        const val SHORTCUT_UNLOCK_ACTION = "com.xxmrk888ytxx.portal.SHORTCUT_UNLOCK_ACTION"
    }
}