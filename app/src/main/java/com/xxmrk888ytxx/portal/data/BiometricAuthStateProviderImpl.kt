package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.biometricauthentication.BiometricAuthManager
import com.xxmrk888ytxx.biometricauthentication.model.BiometricState
import com.xxmrk888ytxx.portal.domain.BiometricAuthStateProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onSubscription
import javax.inject.Inject

class BiometricAuthStateProviderImpl @Inject constructor(
    private val biometricAuthManager: BiometricAuthManager
) : BiometricAuthStateProvider {

    private val _biometricAuthManager = MutableStateFlow(false)

    override val isBiometricAuthAvailable: Flow<Boolean> = _biometricAuthManager.asStateFlow()
        .onSubscription { updateState() }

    override fun updateState() {
        _biometricAuthManager.value = biometricAuthManager.getBiometricState == BiometricState.Available
    }

    init {
        updateState()
    }
}