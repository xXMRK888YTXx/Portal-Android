package com.xxmrk888ytxx.portal.data

import android.app.KeyguardManager
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
import com.xxmrk888ytxx.portal.domain.UnlockMessageSender
import com.xxmrk888ytxx.portal.domain.UnlockRequestManager
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.UnlockScreenActivity
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.UnlockScreenActivity.Companion.EXTRA_UNLOCK_SCREEN_DATA
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.model.UnlockScreenData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.math.abs

class UnlockRequestManagerImpl @Inject constructor(
    private val context: Context,
    private val permissionManager: PermissionManager,
    private val unlockMessageSender: UnlockMessageSender,
) : UnlockRequestManager {

    private val unlockScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService<NotificationManager>()!!
    }

    private val keyguardManager by lazy {
        context.getSystemService<KeyguardManager>()!!
    }

    override fun automaticUnlock(
        clientId: String,
        showUnlockScreenOrUnlockOnlyWhenScreenUnlocked: Boolean,
        request: UnlockServiceRequest
    ) {
        unlockScope.launch {

            if (showUnlockScreenOrUnlockOnlyWhenScreenUnlocked && !waitScreenUnlock()) {
                return@launch
            }

            unlockMessageSender.sendMessage(
                clientId = clientId,
                message = UnlockServiceMessage.Unlock(request.requestId)
            )
        }
    }

    override fun showUnlockScreen(
        clientId: String,
        deviceName: String,
        showUnlockScreenOrUnlockOnlyWhenScreenUnlocked: Boolean,
        request: UnlockServiceRequest
    ) {
        fastDebugLog("showScreenImpl")
        when {
            permissionManager.isShowSystemAlertPermissionGranted -> showActivity(
                clientId,
                request,
                showUnlockScreenOrUnlockOnlyWhenScreenUnlocked
            ).also { fastDebugLog("showActivity") }

            permissionManager.isNotificationPermissionGranted -> sendNotification(
                clientId,
                deviceName,
                request,
                showUnlockScreenOrUnlockOnlyWhenScreenUnlocked
            ).also { fastDebugLog("sendNotification") }

            else -> fastDebugLog("showUnlockScreen canceled because isShowSystemAlertPermissionGranted and isNotificationPermissionGranted permission is not granted")
        }
    }

    private fun showActivity(
        deviceId: String,
        request: UnlockServiceRequest,
        showUnlockScreenOrUnlockOnlyWhenScreenUnlocked: Boolean
    ) {
        unlockScope.launch {
            if (showUnlockScreenOrUnlockOnlyWhenScreenUnlocked && !waitScreenUnlock()) {
                return@launch
            }

            val intent = createIntentForStartUnlockScreen(deviceId, request.requestId).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    override fun sendNotification(
        clientId: String,
        deviceName: String,
        request: UnlockServiceRequest,
        showUnlockScreenOrUnlockOnlyWhenScreenUnlocked: Boolean
    ) {
        unlockScope.launch {
            if (showUnlockScreenOrUnlockOnlyWhenScreenUnlocked && !waitScreenUnlock()) {
                return@launch
            }

            val intent = createIntentForStartUnlockScreen(clientId, request.requestId)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = context.buildNotification(NOTIFICATION_CHANNEL_ID) {
                setSmallIcon(R.drawable.ic_launcher_foreground)
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
                abs(clientId.hashCode()),
                notification
            )
        }
    }

    private fun createIntentForStartUnlockScreen(
        deviceId: String,
        requestId: String?
    ): Intent {
        return Intent(context, UnlockScreenActivity::class.java).apply {
            action = UnlockScreenActivity.UNLOCK_REQUEST_FROM_PC_ACTION
            putExtra(
                EXTRA_UNLOCK_SCREEN_DATA, UnlockScreenData(
                    clientId = deviceId,
                    requestId = requestId
                )
            )
        }
    }

    private suspend fun waitScreenUnlock(): Boolean {
        return withTimeoutOrNull(AWAIT_SCREEN_UNLOCK_TIMEOUT) {
            while (isActive) {
                if (!keyguardManager.isKeyguardLocked) return@withTimeoutOrNull true
                delay(1500)
            }
            return@withTimeoutOrNull false
        } ?: return false
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
        const val AWAIT_SCREEN_UNLOCK_TIMEOUT = 300_000L
    }
}