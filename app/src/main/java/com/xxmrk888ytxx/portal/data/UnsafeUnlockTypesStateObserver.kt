package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.mydictionary.DI.scope.AppScope
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.portal.domain.ShortcutRepository
import com.xxmrk888ytxx.portal.domain.model.UnlockMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AppScope
class UnsafeUnlockTypesStateObserver @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val shortcutRepository: ShortcutRepository,
    private val deviceSettingsRepository: DeviceSettingsRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())


    init {
        scope.launch {
            settingsRepository
                .portalSettings
                .map { it.isUnsafeUnlockTypesDisabled }
                .distinctUntilChanged()
                .collect { isDisabled ->
                    if (isDisabled) {
                        deviceSettingsRepository.getAllDevicesWithNotSecureUnlockMethod().forEach {
                            deviceSettingsRepository.updateUnlockMethod(it.deviceId, UnlockMethod.Notification)
                        }
                        shortcutRepository.getShortcutWithInsecureUnlock().forEach {
                            shortcutRepository.updateIsRequiredBiometricUnlock(it.shortcutId, true)
                        }
                    }
                }
        }
    }
}