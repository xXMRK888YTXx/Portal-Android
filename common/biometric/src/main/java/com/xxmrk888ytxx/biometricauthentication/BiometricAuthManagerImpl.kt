package com.xxmrk888ytxx.biometricauthentication

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.ERROR_CANCELED
import androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON
import androidx.biometric.BiometricPrompt.ERROR_TIMEOUT
import androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.biometricauthentication.model.BiometricAuthOptions
import com.xxmrk888ytxx.biometricauthentication.model.BiometricState
import com.xxmrk888ytxx.coreandroid.fastDebugLog
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
                    fastDebugLog("onAuthenticationSucceeded")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    when (errorCode) {
                        ERROR_NEGATIVE_BUTTON, ERROR_TIMEOUT, ERROR_CANCELED, ERROR_USER_CANCELED -> options.onCanceled()
                        else -> options.onError()
                    }
                    fastDebugLog("onAuthenticationError $errorCode, $errString")

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    options.onFailed()
                    fastDebugLog("onAuthenticationFailed")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(options.title)
            setDescription(options.description)
            setSubtitle(options.subTitle)
            setAllowedAuthenticators(getAllowedAuthenticators(options))
            if (!options.allowPasswordAuth)
                setNegativeButtonText(options.negativeButtonText)
        }.build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun getAllowedAuthenticators(options: BiometricAuthOptions) : Int {
        if (options.allowPasswordAuth) return BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return if (isStrongBiometricAvailable) BIOMETRIC_STRONG else BIOMETRIC_WEAK
    }

    private val isStrongBiometricAvailable: Boolean
        get() = biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS

}