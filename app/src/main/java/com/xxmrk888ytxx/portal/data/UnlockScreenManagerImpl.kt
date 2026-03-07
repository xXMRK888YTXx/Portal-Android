package com.xxmrk888ytxx.portal.data

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.UnlockScreenManager
import com.xxmrk888ytxx.portal.domain.model.Device
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.UnlockScreenActivity
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.UnlockScreenActivity.Companion.EXTRA_UNLOCK_SCREEN_DATA
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.model.UnlockScreenData
import javax.inject.Inject
import kotlin.random.Random

class UnlockScreenManagerImpl @Inject constructor(
    private val context: Context
) : UnlockScreenManager {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService<NotificationManager>()!!
    }


    @SuppressLint("FullScreenIntentPolicy")
    override fun showUnlockScreen(device: Device) {
        val fullScreenIntent = Intent(context, UnlockScreenActivity::class.java).apply {
            putExtra(EXTRA_UNLOCK_SCREEN_DATA, UnlockScreenData(device.deviceId))
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = context.buildNotification(NOTIFICATION_CHANNEL_ID) {
            // TODO change icon
            setSmallIcon(com.xxmrk888ytxx.mainscreen.R.drawable.lock_open)
            setContentTitle(context.getString(R.string.is_requesting_unlocking, device.deviceName))
            setContentText("Click to allow")
            setFullScreenIntent(fullScreenPendingIntent, true)
            setAutoCancel(true)
            build()
        }
        notificationManager.notify(
            Random(System.currentTimeMillis()).nextInt(1, Int.MAX_VALUE),
            notification
        )
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