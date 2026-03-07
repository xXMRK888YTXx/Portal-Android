package com.xxmrk888ytxx.biometricauthentication

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.biometricauthentication.model.BiometricAuthOptions
import com.xxmrk888ytxx.biometricauthentication.model.BiometricState

interface BiometricAuthManager {
    val getBiometricState: BiometricState
    fun requestBiometricAuth(
        activity: FragmentActivity,
        requestOptions: BiometricAuthOptions.() -> Unit
    )
    companion object
}