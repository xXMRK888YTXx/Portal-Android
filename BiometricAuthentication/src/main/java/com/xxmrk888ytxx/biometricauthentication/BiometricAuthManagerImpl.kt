package com.xxmrk888ytxx.biometricauthentication

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.biometricauthentication.model.BiometricAuthOptions
import com.xxmrk888ytxx.biometricauthentication.model.BiometricState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

internal class BiometricAuthManagerImpl(
    private val context: Context
) : BiometricAuthManager {

    private val biometricManager by lazy {
        BiometricManager.from(context)
    }
    override val getBiometricState: BiometricState
        get() {
            if (isStrongBiometricAvailable) return BiometricState.Available

            return when (biometricManager.canAuthenticate(BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_SUCCESS -> BiometricState.Available
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricState.NotHardware
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricState.NotAvailable
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricState.NotEnrolled
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricState.SecurityUpdateRequired
                else -> BiometricState.Unknown
            }
        }


    override fun requestBiometricAuth(
        activity: FragmentActivity,
        requestOptions: BiometricAuthOptions.() -> Unit
    ) {
        val options = BiometricAuthOptions().apply(requestOptions)

        val biometricPrompt = BiometricPrompt(
            /* activity = */ activity,
            /* executor = */ options.executor ?: Dispatchers.Default.asExecutor(),
            /* callback = */ object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    options.onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    options.onError()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    options.onFailed()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(options.title)
            .setDescription(options.description)
            .setSubtitle(options.subTitle)
            .setNegativeButtonText(options.negativeButtonText)
            .setAllowedAuthenticators(if (isStrongBiometricAvailable) BIOMETRIC_STRONG else BIOMETRIC_WEAK)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private val isStrongBiometricAvailable: Boolean
        get() = biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS

}