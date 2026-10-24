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

    private val aliasSuffixes = mapOf(
        "default" to "MainActivityDefault",
        "tactical" to "MainActivityTactical",
        "minimal" to "MainActivityMinimal",
        "soft" to "MainActivitySoft",
        "anime_1" to "MainActivityAnime1",
        "anime_2" to "MainActivityAnime2",
        "classic" to "MainActivityClassic"
    )

    override fun setAppIcon(iconId: String) {
        val packageManager = context.packageManager
        val packageName = context.packageName
        val targetSuffix = aliasSuffixes[iconId] ?: aliasSuffixes["default"]!!
        val targetAlias = "$packageName.$targetSuffix"

        fastDebugLog("Changing app icon to $iconId (target alias: $targetAlias)")

        // 1. Enable target alias FIRST so there is never a state where no launcher component is active
        val targetComponent = ComponentName(packageName, targetAlias)
        var targetEnabledSuccessfully = false
        try {
            val currentState = packageManager.getComponentEnabledSetting(targetComponent)
            if (currentState != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                packageManager.setComponentEnabledSetting(
                    targetComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
            targetEnabledSuccessfully = true
        } catch (e: Exception) {
            fastDebugLog("Failed to enable target component $targetAlias: ${e.message}")
        }

        if (!targetEnabledSuccessfully) {
            fastDebugLog("Aborting disabling other aliases because target could not be enabled")
            return
        }

        // 2. Disable all other aliases SECOND
        aliasSuffixes.values.forEach { suffix ->
            val aliasName = "$packageName.$suffix"
            if (aliasName == targetAlias) return@forEach

            val component = ComponentName(packageName, aliasName)
            try {
                val currentState = packageManager.getComponentEnabledSetting(component)
                if (currentState != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                    packageManager.setComponentEnabledSetting(
                        component,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
            } catch (e: Exception) {
                fastDebugLog("Failed to disable component $aliasName: ${e.message}")
            }
        }
    }
}
