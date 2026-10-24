package com.xxmrk888ytxx.portal.data

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.AppIconManager
import javax.inject.Inject

class AppIconManagerImpl @Inject constructor(
    private val context: Context
) : AppIconManager {

    private val aliases = mapOf(
        "default" to "com.xxmrk888ytxx.portal.MainActivityDefault",
        "tactical" to "com.xxmrk888ytxx.portal.MainActivityTactical",
        "minimal" to "com.xxmrk888ytxx.portal.MainActivityMinimal",
        "soft" to "com.xxmrk888ytxx.portal.MainActivitySoft",
        "anime_1" to "com.xxmrk888ytxx.portal.MainActivityAnime1",
        "anime_2" to "com.xxmrk888ytxx.portal.MainActivityAnime2",
        "classic" to "com.xxmrk888ytxx.portal.MainActivityClassic"
    )

    override fun setAppIcon(iconId: String) {
        val packageManager = context.packageManager
        val packageName = context.packageName
        val targetAlias = aliases[iconId] ?: aliases["default"]!!

        fastDebugLog("Changing app icon to $iconId (target alias: $targetAlias)")

        aliases.values.forEach { aliasName ->
            val component = ComponentName(packageName, aliasName)
            val desiredState = if (aliasName == targetAlias) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            try {
                val currentState = packageManager.getComponentEnabledSetting(component)
                if (currentState != desiredState) {
                    packageManager.setComponentEnabledSetting(
                        component,
                        desiredState,
                        PackageManager.DONT_KILL_APP
                    )
                }
            } catch (e: Exception) {
                fastDebugLog("Failed to set component state for $aliasName: ${e.message}")
            }
        }
    }
}
