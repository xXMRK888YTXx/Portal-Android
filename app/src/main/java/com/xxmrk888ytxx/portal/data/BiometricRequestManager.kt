package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.mydictionary.DI.scope.AppScope
import com.xxmrk888ytxx.portal.domain.BiometricActivityResultReceiver
import com.xxmrk888ytxx.portal.domain.BiometricRequestController
import com.xxmrk888ytxx.portal.domain.model.BiometricAuthResult
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.jvm.Throws

@AppScope
class BiometricRequestManager @Inject constructor() : BiometricRequestController, BiometricActivityResultReceiver {

    private val _biometricAuthRequestForActivity = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1
    )

    override val biometricAuthRequestForActivity: Flow<Unit> = _biometricAuthRequestForActivity.asSharedFlow()

    private val _biometricAuthResult = MutableSharedFlow<BiometricAuthResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val biometricAuthResult = MutableSharedFlow<BiometricAuthResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).onSubscription { _biometricAuthRequestForActivity.emit(Unit) }

    @Throws(TimeoutCancellationException::class)
    override suspend fun waitBiometricAuthResult(timeout: Long): BiometricAuthResult = withTimeout(timeout) {
        biometricAuthResult.first()
    }

    override fun onNewBiometricAuthResult(result: BiometricAuthResult) {
        _biometricAuthResult.tryEmit(result)
    }
}