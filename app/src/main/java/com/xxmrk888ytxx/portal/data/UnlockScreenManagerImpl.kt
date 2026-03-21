package com.xxmrk888ytxx.portal.data

import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.PermissionManager
import com.xxmrk888ytxx.portal.domain.UnlockScreenManager
import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest
import com.xxmrk888ytxx.portal.domain.model.WifiDevice
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.UnlockScreenActivity
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.UnlockScreenActivity.Companion.EXTRA_UNLOCK_SCREEN_DATA
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.model.UnlockScreenData
import javax.inject.Inject
import kotlin.random.Random

class UnlockScreenManagerImpl @Inject constructor(
    private val context: Context,
    private val permissionManager: PermissionManager
) : UnlockScreenManager {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService<NotificationManager>()!!
    }


    override fun showUnlockScreen(wifiDevice: WifiDevice, request: UnlockServiceRequest) {
        fastDebugLog("showUnlockScreen wifiDevice")
        showScreenImpl(wifiDevice.deviceId, wifiDevice.deviceName, request)
    }

    override fun showUnlockScreen(
        bluetoothDevice: BluetoothDevice,
        request: UnlockServiceRequest
    ) {
        fastDebugLog("showUnlockScreen bluetoothDevice")
        showScreenImpl(bluetoothDevice.clientId, bluetoothDevice.clientId, request)
    }

    private fun showScreenImpl(
        deviceId: String,
        deviceName: String,
        request: UnlockServiceRequest
    ) {
        fastDebugLog("showScreenImpl")
        when {
            permissionManager.isShowSystemAlertPermissionGranted -> showActivity(
                deviceId,
                request
            ).also { fastDebugLog("showActivity") }

            permissionManager.isNotificationPermissionGranted -> sendNotification(
                deviceId,
                deviceName,
                request
            ).also { fastDebugLog("sendNotification") }

            else -> fastDebugLog("showUnlockScreen canceled because isShowSystemAlertPermissionGranted and isNotificationPermissionGranted permission is not granted")
        }
    }

    private fun showActivity(deviceId: String, request: UnlockServiceRequest) {
        val intent = createIntentForStartUnlockScreen(deviceId, request.requestId).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun sendNotification(
        deviceId: String,
        deviceName: String,
        request: UnlockServiceRequest
    ) {
        val intent = createIntentForStartUnlockScreen(deviceId, request.requestId)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = context.buildNotification(NOTIFICATION_CHANNEL_ID) {
            // TODO change icon
            setSmallIcon(com.xxmrk888ytxx.mainscreen.R.drawable.lock_open)
            setContentTitle(
                context.getString(
                    R.string.is_requesting_unlocking,
                    deviceName
                )
            )
            setContentText(context.getString(R.string.click_to_allow))
            setAutoCancel(true)
            setContentIntent(pendingIntent)
            build()
        }
        notificationManager.notify(
            Random(System.currentTimeMillis()).nextInt(1, Int.MAX_VALUE),
            notification
        )
    }

    private fun createIntentForStartUnlockScreen(
        deviceId: String,
        requestId: String?
    ): Intent {
        return Intent(context, UnlockScreenActivity::class.java).apply {
            putExtra(
                EXTRA_UNLOCK_SCREEN_DATA, UnlockScreenData(
                    clientId = deviceId,
                    requestId = requestId
                )
            )
        }
    }

    init {
        context.buildNotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.requests_for_unlocking)
        ) {
            importance = IMPORTANCE_HIGH
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "UnlockNotificationChannel"
    }
}