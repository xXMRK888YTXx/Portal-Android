package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.mydictionary.DI.scope.AppScope
import com.xxmrk888ytxx.portal.domain.BiometricActivityResultReceiver
import com.xxmrk888ytxx.portal.domain.BiometricRequestController
import com.xxmrk888ytxx.portal.domain.model.BiometricAuthRequestOption
import com.xxmrk888ytxx.portal.domain.model.BiometricAuthResult
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@AppScope
class BiometricRequestManager @Inject constructor() : BiometricRequestController,
    BiometricActivityResultReceiver {

    private val _biometricAuthRequestForActivity = MutableSharedFlow<BiometricAuthRequestOption>(
        extraBufferCapacity = 1
    )

    override val biometricAuthRequestForActivity: Flow<BiometricAuthRequestOption> =
        _biometricAuthRequestForActivity.asSharedFlow()

    private val _biometricAuthResult = MutableSharedFlow<BiometricAuthResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val biometricAuthResult = _biometricAuthResult.asSharedFlow()

    @Throws(TimeoutCancellationException::class)
    override suspend fun waitBiometricAuthResult(
        timeout: Long,
        dialogDescription: String?
    ): BiometricAuthResult = withTimeout(timeout) {
        _biometricAuthRequestForActivity.emit(BiometricAuthRequestOption(description = dialogDescription))
        biometricAuthResult.first()
    }

    override fun onNewBiometricAuthResult(result: BiometricAuthResult) {
        _biometricAuthResult.tryEmit(result)
    }
}