package com.xxmrk888ytxx.portal.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.UnlockServiceManager
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest
import com.xxmrk888ytxx.portal.exception.ServiceControllerException
import com.xxmrk888ytxx.unlockservice.core.IdleModDetectedCallback
import com.xxmrk888ytxx.unlockservice.core.UnlockMessage
import com.xxmrk888ytxx.unlockservice.core.UnlockRequest
import com.xxmrk888ytxx.unlockservice.core.UnlockService
import com.xxmrk888ytxx.unlockservice.core.UnlockServiceController
import com.xxmrk888ytxx.unlockservice.wifiService.WifiUnlockService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class UnlockServiceManagerImpl @Inject constructor(
    private val context: Context,
) : UnlockServiceManager, ServiceConnection, IdleModDetectedCallback {

    private val _wifiServiceController = MutableStateFlow<UnlockServiceController?>(null)
    private val wifiServiceMutex = Mutex()

    override suspend fun startListeningUnlockRequest(clientId: String): Result<Flow<UnlockServiceRequest>> = wrapServiceOperation {
        val controller = connectToWifiService()
        controller.startListeningUnlockRequest(clientId).map {
            when (it) {
                is UnlockRequest.Auth -> UnlockServiceRequest.Auth(it.requestId)
            }
        }
    }

    override suspend fun stopListeningUnlockRequest(clientId: String): Result<Unit> = wrapServiceOperation {
        val controller = connectToWifiService()
        controller.stopListeningUnlockRequest(clientId)
    }

    private suspend fun <T> wrapServiceOperation(block: suspend () -> T): Result<T> = runCatching {
        try {
            block()
        } catch (e: Exception) {
            throw ServiceControllerException("Error while executing service operation. Exception ${e.message}")
        }
    }


    override suspend fun sendMessageToHost(
        clientId: String,
        message: UnlockServiceMessage
    ): Result<Unit> = wrapServiceOperation {
        val controller = connectToWifiService()
        val message = when (message) {
            is UnlockServiceMessage.Unlock -> UnlockMessage.ApproveUnlock(requestId = message.requestId)
            is UnlockServiceMessage.Canceled -> UnlockMessage.Canceled(requestId = message.requestId)
        }
        controller.sendMessage(clientId, message)
    }

    private suspend fun waitForWifiServiceController(): UnlockServiceController =
        _wifiServiceController.value?.let { return it } ?: _wifiServiceController.filterNotNull().first()

    private suspend fun connectToWifiService(): UnlockServiceController = wifiServiceMutex.withLock {
        val currentController = _wifiServiceController.value
        if (currentController != null) return currentController

        Intent(context, WifiUnlockService::class.java).apply {
            context.bindService(
                this,
                this@UnlockServiceManagerImpl,
                Context.BIND_AUTO_CREATE
            )
        }
        return waitForWifiServiceController()
    }

    override fun onServiceConnected(
        name: ComponentName?,
        service: IBinder?
    ) {
        fastDebugLog("$name onServiceConnected")

        val controller = (service as? UnlockService.UnlockBinder)?.controller
        controller?.setIdleModCallback(this)
        when (name?.className) {
            WifiUnlockService::class.qualifiedName -> _wifiServiceController.value = controller
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        fastDebugLog("$name onServiceDisconnected")
        when (name?.className) {
            WifiUnlockService::class.qualifiedName -> _wifiServiceController.value = null
        }
    }

    override fun isCanStopService(): Boolean {
        context.unbindService(this)
        _wifiServiceController.value = null
        return true
    }
}