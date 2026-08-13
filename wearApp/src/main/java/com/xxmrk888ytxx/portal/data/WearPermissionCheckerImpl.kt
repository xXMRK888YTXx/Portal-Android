package com.xxmrk888ytxx.portal.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.xxmrk888ytxx.portal.domain.WearPermissionChecker
import com.xxmrk888ytxx.portal.domain.WearPermissionState
import javax.inject.Inject

class WearPermissionCheckerImpl @Inject constructor(
    private val context: Context
) : WearPermissionChecker {
    override fun getState(): WearPermissionState {
        val canPostNotifications = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

        return WearPermissionState(
            canPostNotifications = canPostNotifications,
            canDrawOverlays = Settings.canDrawOverlays(context)
        )
    }
}
