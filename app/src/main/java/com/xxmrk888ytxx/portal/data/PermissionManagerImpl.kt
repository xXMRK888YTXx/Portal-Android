package com.xxmrk888ytxx.portal.data

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.portal.domain.PermissionManager
import javax.inject.Inject
import androidx.core.net.toUri

class PermissionManagerImpl @Inject constructor(
    private val context: Context
) : PermissionManager {

    override val isNotificationPermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PERMISSION_GRANTED
        } else {
            true
        }
    override val isNearbyDevicesPermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES) == PERMISSION_GRANTED
        } else {
            true
        }

    override val isShowFullIntentPermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            context.getSystemService<NotificationManager>()?.canUseFullScreenIntent() ?: false
        } else {
            true
        }

    override fun requestShowFullScreenIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val intent = Intent(ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                data = "package:${context.packageName}".toUri()
                flags = FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}