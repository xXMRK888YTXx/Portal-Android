package com.xxmrk888ytxx.portal.data

import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.IncomingRequestPresenter
import com.xxmrk888ytxx.portal.domain.WearPermissionChecker
import com.xxmrk888ytxx.portal.domain.WearSettingsRepository
import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest
import com.xxmrk888ytxx.portal.presentation.mainActivity.MainActivity
import javax.inject.Inject
import kotlin.math.abs

class IncomingRequestPresenterImpl @Inject constructor(
    private val context: Context,
    private val permissionChecker: WearPermissionChecker,
    private val settingsRepository: WearSettingsRepository
) : IncomingRequestPresenter {

    private val keyguardManager: KeyguardManager by lazy {
        context.getSystemService<KeyguardManager>()!!
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    override fun present(request: IncomingUnlockRequest) {
        ensureNotificationChannel()
        val permissionState = permissionChecker.getState()
        val isLocked = keyguardManager.isKeyguardLocked
        val shouldTryOpenScreen = permissionState.canDrawOverlays &&
                (!isLocked || settingsRepository.showRequestsOnLockedScreen.value)

        if (shouldTryOpenScreen && tryOpenRequestScreen(request.decisionId)) {
            return
        }

        if (permissionState.canPostNotifications) {
            showNotification(request)
        }
    }

    override fun cancel(decisionId: String) {
        notificationManager.cancel(abs(decisionId.hashCode()))
    }

    private fun tryOpenRequestScreen(decisionId: String): Boolean = runCatching {
        context.startActivity(createOpenRequestIntent(decisionId).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }.isSuccess

    private fun showNotification(request: IncomingUnlockRequest) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            abs(request.decisionId.hashCode()),
            createOpenRequestIntent(request.decisionId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.unlock_request_title, request.deviceName))
            .setContentText(context.getString(R.string.tap_to_open_request))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(abs(request.decisionId.hashCode()), notification)
    }

    private fun createOpenRequestIntent(decisionId: String): Intent {
        return Intent(context, MainActivity::class.java).apply {
            action = MainActivity.ACTION_OPEN_REQUEST
            putExtra(MainActivity.EXTRA_DECISION_ID, decisionId)
        }
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
