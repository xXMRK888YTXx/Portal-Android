package com.xxmrk888ytxx.portal.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.xxmrk888ytxx.portal.domain.PermissionManager
import javax.inject.Inject

class PermissionManagerImpl @Inject constructor(
    private val context: Context
) : PermissionManager {

    private val powerManager: PowerManager by lazy { context.getSystemService<PowerManager>()!! }

    override val isNotificationPermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PERMISSION_GRANTED
        } else {
            true
        }

    override val isNearbyDevicesPermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PERMISSION_GRANTED
        } else {
            true
        }

    override val isShowSystemAlertPermissionGranted: Boolean
        get() = Settings.canDrawOverlays(context)

    override val isIgnoreBatteryOptimizationsPermissionGranted: Boolean
        get() = powerManager.isIgnoringBatteryOptimizations(context.packageName)

    override fun requestShowSystemAlertPermission() {
        val intent = Intent(ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = "package:${context.packageName}".toUri()
            flags = FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    @SuppressLint("BatteryLife")
    override fun requestIgnoreBatteryOptimizations() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = "package:${context.packageName}".toUri()
            flags = FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}