package com.xxmrk888ytxx.portal.data

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.biometricauthentication.BiometricAuthManager
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.BiometricDialogController
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.portal.domain.model.BiometricDialogEvent
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BiometricDialogControllerImpl @Inject constructor(
    private val biometricAuthManager: BiometricAuthManager,
    private val context: Context,
    private val settingsRepository: SettingsRepository
) : BiometricDialogController {
    override suspend fun sendRequest(
        activity: FragmentActivity,
        description: String?,
        onEvent: (BiometricDialogEvent) -> Unit
    ) {
        val isPasswordAuthAllowed = settingsRepository.portalSettings.first().isAdditionalPasswordAuthEnabled
        biometricAuthManager.requestBiometricAuth(activity) {
            onSuccess = { onEvent(BiometricDialogEvent.Success) }
            onError = { onEvent(BiometricDialogEvent.Error) }
            onFailed = { onEvent(BiometricDialogEvent.Failed) }
            onCanceled = { onEvent(BiometricDialogEvent.Canceled) }

            title = context.getString(R.string.confirm_your_identity)
            subTitle = description ?: context.getString(R.string.confirm_your_identity)
            allowPasswordAuth = isPasswordAuthAllowed
        }
    }
}