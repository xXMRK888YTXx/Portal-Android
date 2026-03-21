package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.AwaitUnlockRequestManager
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.UnlockRequestHandler
import com.xxmrk888ytxx.portal.domain.UnlockServiceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.mutableSetOf

class AwaitUnlockRequestManagerImpl @Inject constructor(
    private val unlockServiceManager: UnlockServiceManager,
    private val unlockRequestHandler: UnlockRequestHandler,
    private val deviceServiceManager: DeviceSettingsRepository
) : AwaitUnlockRequestManager {

    private val _enabledListeners = MutableStateFlow(mutableSetOf<String>())

    private val awaitUnlockRequestManagerScope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val settingsObserverJob =
        awaitUnlockRequestManagerScope.launch(start = CoroutineStart.LAZY) {
            val knownDeviceIds = mutableSetOf<String>()
            deviceServiceManager.deviceSettings.collect { deviceSettings ->
                val currentDeviceIds = deviceSettings.map { it.deviceId }.toSet()
                val removedDeviceIds = knownDeviceIds - currentDeviceIds
                removedDeviceIds.forEach { deviceId ->
                    disableForDevice(deviceId)
                }

                knownDeviceIds.clear()
                knownDeviceIds.addAll(currentDeviceIds)

                deviceSettings.forEach {
                    when (it.awaitUnlockRequests) {
                        true -> enableForDevice(it.deviceId)
                        false -> disableForDevice(it.deviceId)
                    }
                }
            }

        }

    private suspend fun enableForDevice(clientId: String) {
        if (_enabledListeners.value.contains(clientId)) return
        unlockServiceManager.startListeningUnlockRequest(clientId)
            .onSuccess { flow ->
                _enabledListeners.value.add(clientId)
                awaitUnlockRequestManagerScope.launch {
                    flow.collect { request ->
                        unlockRequestHandler.onNewRequest(clientId, request)
                    }
                }.invokeOnCompletion {
                    _enabledListeners.value.remove(clientId)
                }
            }
            .onFailure { fastDebugLog(it) }
    }

    private suspend fun disableForDevice(clientId: String) {
        unlockServiceManager.stopListeningUnlockRequest(clientId)
    }

    override fun restoreUnlockState() {
        // Auto restore in init block
    }

    init {
        settingsObserverJob.start()
    }
}