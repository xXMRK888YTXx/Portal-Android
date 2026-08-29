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
    var title: String = "Title",
    var subTitle: String? = null,
    var negativeButtonText: String = "Cancel",
    var description: String? = null,
    var allowPasswordAuth: Boolean = false,
    var onSuccess: () -> Unit = {},
    var onError: () -> Unit = {},
    var onFailed: () -> Unit = {},
    var onCanceled: () -> Unit = {}
)


