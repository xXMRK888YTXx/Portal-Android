package com.xxmrk888ytxx.portal.data

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.IncomingRequestPresenter
import com.xxmrk888ytxx.portal.domain.WearPermissionChecker
import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest
import com.xxmrk888ytxx.portal.presentation.mainActivity.MainActivity
import javax.inject.Inject
import kotlin.math.abs

class IncomingRequestPresenterImpl @Inject constructor(
    private val context: Context,
    private val permissionChecker: WearPermissionChecker
) : IncomingRequestPresenter {

    private val notificationManager = NotificationManagerCompat.from(context)

    override fun present(request: IncomingUnlockRequest) {
        ensureNotificationChannel()
        val permissionState = permissionChecker.getState()
        if (!permissionState.canPostNotifications) {
            return
        }

        val requestPendingIntent = createRequestPendingIntent(request.decisionId)
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.unlock_request_title, request.deviceName))
            .setContentText(context.getString(R.string.tap_to_open_request))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(requestPendingIntent)

        try {
            notificationManager.notify(
                abs(request.decisionId.hashCode()),
                notificationBuilder.build()
            )
        } catch (_: SecurityException) {
            // Permission can be revoked between the explicit check and notify().
        }
    }

    override fun cancel(decisionId: String) {
        notificationManager.cancel(abs(decisionId.hashCode()))
    }

    private fun createRequestPendingIntent(decisionId: String): PendingIntent {
        return PendingIntent.getActivity(
            context,
            abs(decisionId.hashCode()),
            Intent(context, MainActivity::class.java).apply {
                action = MainActivity.ACTION_OPEN_REQUEST
                putExtra(MainActivity.EXTRA_DECISION_ID, decisionId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        notificationManager.createNotificationChannel(
            NotificationChannelCompat.Builder(
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH
            )
                .setName(context.getString(R.string.unlock_requests_channel))
                .build()
        )
    }

    private companion object {
        const val CHANNEL_ID = "wear_unlock_requests"
    }
}
