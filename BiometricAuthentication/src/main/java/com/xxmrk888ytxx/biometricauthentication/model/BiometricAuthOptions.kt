package com.xxmrk888ytxx.biometricauthentication.model

import java.util.concurrent.Executor

/**
 * Configuration options for launching biometric authentication.
 *
 * Used together with [BiometricPrompt] to configure the appearance of the authentication
 * dialog and handle authentication results.
 *
 * @property executor Executor used to run callbacks. If `null`, the thread executor
 * is used ([kotlinx.coroutines.Dispatchers.Default.asExecutor()]).
 * @property onSuccess Invoked when authentication completes successfully.
 * @property onError Invoked on an unrecoverable error or user cancellation
 * (e.g. too many failed attempts, negative button pressed).
 * @property onFailed Invoked when an authentication attempt is rejected but retrying
 * is still possible (e.g. fingerprint not recognized).
 * @property title Title displayed in the biometric authentication dialog.
 * @property subTitle Subtitle displayed below the title in the dialog.
 * @property negativeButtonText Label for the negative/cancel button in the dialog.
 * @property description Additional description displayed in the dialog.
 */
@ConsistentCopyVisibility
data class BiometricAuthOptions internal constructor(
    var executor: Executor? = null,
    var onSuccess: () -> Unit = {},
    var onError: () -> Unit = {},
    var onFailed: () -> Unit = {},
    var title: String = "Title",
    var subTitle: String? = null,
    var negativeButtonText: String = "Cancel",
    var description: String? = null,
)

/**
 * Sets a single callback for all types of authentication failure.
 *
 * A convenience alternative to setting [BiometricAuthOptions.onError] and
 * [BiometricAuthOptions.onFailed] separately, when the reason for failure
 * does not need to be distinguished.
 *
 * @param onRequestFailed Callback invoked on both unrecoverable errors
 * ([BiometricAuthOptions.onError]) and rejected attempts ([BiometricAuthOptions.onFailed]).
 *
 * @see BiometricAuthOptions.onError
 * @see BiometricAuthOptions.onFailed
 */
fun BiometricAuthOptions.setOnRequestFailed(
    onRequestFailed: () -> Unit
) {
    onError = onRequestFailed
    onFailed = onRequestFailed
}


