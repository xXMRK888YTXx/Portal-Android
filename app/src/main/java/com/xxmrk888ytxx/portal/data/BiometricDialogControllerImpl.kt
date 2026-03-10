package com.xxmrk888ytxx.portal.data

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.biometricauthentication.BiometricAuthManager
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.BiometricDialogController
import com.xxmrk888ytxx.portal.domain.model.BiometricDialogEvent
import javax.inject.Inject

class BiometricDialogControllerImpl @Inject constructor(
    private val biometricAuthManager: BiometricAuthManager,
    private val context: Context
) : BiometricDialogController {
    override suspend fun sendRequest(
        activity: FragmentActivity,
        onEvent: (BiometricDialogEvent) -> Unit
    ) {
        biometricAuthManager.requestBiometricAuth(activity) {
            onSuccess = { onEvent(BiometricDialogEvent.Success) }
            onError = { onEvent(BiometricDialogEvent.Error) }
            onFailed = { onEvent(BiometricDialogEvent.Failed) }
            onCanceled = { onEvent(BiometricDialogEvent.Canceled) }

            title = context.getString(R.string.verify_with_biometrics)
            subTitle = context.getString(R.string.confirm_your_identity)
        }
    }
}