package com.xxmrk888ytxx.portal.data

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.biometricauthentication.BiometricAuthManager
import com.xxmrk888ytxx.biometricauthentication.model.setOnRequestFailed
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.BiometricDialogController
import javax.inject.Inject

class BiometricDialogControllerImpl @Inject constructor(
    private val biometricAuthManager: BiometricAuthManager,
    private val context: Context
) : BiometricDialogController {
    override suspend fun sendRequest(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        biometricAuthManager.requestBiometricAuth(activity) {
            this@requestBiometricAuth.onSuccess = onSuccess
            setOnRequestFailed(onFailed)

            title = context.getString(R.string.verify_with_biometrics)
            subTitle = context.getString(R.string.confirm_your_identity)
        }
    }
}