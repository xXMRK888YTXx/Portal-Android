package com.xxmrk888ytxx.portal.providedContract.settingsScreen

import com.xxmrk888ytxx.portal.domain.BiometricAuthStateProvider
import com.xxmrk888ytxx.settingsscreen.contract.BiometricProtectionAvailableStateProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BiometricProtectionAvailableStateProviderImpl @Inject constructor(
    private val biometricAuthStateProvider: BiometricAuthStateProvider
) : BiometricProtectionAvailableStateProvider {
    override val isAvailable: Flow<Boolean> = biometricAuthStateProvider.isBiometricAuthAvailable
}