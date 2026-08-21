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
import com.xxmrk888ytxx.portal.data.broadcastReceiver.WearNotificationActionReceiver
import com.xxmrk888ytxx.portal.domain.IncomingRequestPresenter
import com.xxmrk888ytxx.portal.domain.WearPermissionChecker
import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest
import com.xxmrk888ytxx.portal.presentation.incomingRequest.IncomingRequestActivity
import javax.inject.Inject
import kotlin.math.abs

/**
 * Presenter for incoming unlock requests on Wear OS.
 *
 * Provides notification with Allow and Deny actions, while tapping the notification body
 * opens [IncomingRequestActivity] to display the request details screen.
 */
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
        val allowPendingIntent = createActionPendingIntent(
            decisionId = request.decisionId,
            action = WearNotificationActionReceiver.ACTION_ALLOW,
            requestCodeOffset = 1
        )
        val denyPendingIntent = createActionPendingIntent(
            decisionId = request.decisionId,
            action = WearNotificationActionReceiver.ACTION_DENY,
            requestCodeOffset = 2
        )

        val allowAction = NotificationCompat.Action.Builder(
            R.drawable.check,
            context.getString(R.string.allow),
            allowPendingIntent
        ).build()

        val denyAction = NotificationCompat.Action.Builder(
            R.drawable.close,
            context.getString(R.string.deny),
            denyPendingIntent
        ).build()

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.unlock_request_title, request.deviceName))
            .setContentText(context.getString(R.string.tap_to_open_request))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(requestPendingIntent)
            .addAction(denyAction)
            .addAction(allowAction)

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
            abs(decisionId.hashCode()) * 10,
            Intent(context, IncomingRequestActivity::class.java).apply {
                putExtra(IncomingRequestActivity.EXTRA_DECISION_ID, decisionId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createActionPendingIntent(
        decisionId: String,
        action: String,
        requestCodeOffset: Int
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            abs(decisionId.hashCode()) * 10 + requestCodeOffset,
            Intent(context, WearNotificationActionReceiver::class.java).apply {
                this.action = action
                putExtra(WearNotificationActionReceiver.EXTRA_DECISION_ID, decisionId)
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
