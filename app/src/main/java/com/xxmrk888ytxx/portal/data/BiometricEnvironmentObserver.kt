package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.mydictionary.DI.scope.AppScope
import com.xxmrk888ytxx.portal.domain.BiometricEnvironmentEventHandler
import com.xxmrk888ytxx.portal.domain.SecureStorage
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AppScope
class BiometricEnvironmentObserver @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val secureStorage: SecureStorage,
    private val biometricEnvironmentEventHandler: BiometricEnvironmentEventHandler
) {
    private var isStarted = false

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var observeEnvironmentJob: Job? = null


    fun startObserve() {
        if (isStarted) return
        isStarted = true
        fastDebugLog("startObserve")
        scope.launch {
            settingsRepository
                .portalSettings
                .map { it.isRemovePairedClientsIfBiometricEnvironmentChangedEnabled }
                .distinctUntilChanged()
                .collect {
                    if (it) enableObserver() else disableObserver()
                }
        }
    }

    private fun disableObserver() {
        observeEnvironmentJob?.cancel()
        observeEnvironmentJob = null
        scope.launch { secureStorage.resetKeyForObserveEnvironment() }
    }

    private fun enableObserver() {
        observeEnvironmentJob = scope.launch {
            while (isActive) {
                if (secureStorage.isHaveChangesInBiometricEnvironment()) {
                    biometricEnvironmentEventHandler.onBiometricEnvironmentChanged()
                }
                delay(5000)
            }
        }.also { it.invokeOnCompletion { observeEnvironmentJob = null } }
    }
}