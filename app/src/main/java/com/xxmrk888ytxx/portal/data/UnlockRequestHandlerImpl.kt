package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.UnlockMessageSender
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.UnlockRequestHandler
import com.xxmrk888ytxx.portal.domain.UnlockScreenManager
import com.xxmrk888ytxx.portal.domain.UnlockServiceManager
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class UnlockRequestHandlerImpl @Inject constructor(
    private val unlockMessageSender: UnlockMessageSender,
    private val unlockScreenManager: UnlockScreenManager,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository,
) : UnlockRequestHandler {

    private val _handledRequestsId = MutableStateFlow(emptySet<String>())


    override suspend fun onNewRequest(
        clientId: String,
        request: UnlockServiceRequest
    ) {
        if (request.requestId != null && _handledRequestsId.value.contains(request.requestId)) return
        request.requestId?.let { requestId ->
            _handledRequestsId.update { it + requestId }
        }
        fastDebugLog("onNewRequest: $request")
        //unlockServiceManager.sendMessageToHost(clientId, UnlockServiceMessage.Unlock)
        val wifiDevice = wifiDeviceRepository.getDeviceById(clientId).first()
        val bluetoothDevice = bluetoothDeviceRepository.getDeviceById(clientId).first()

        when {
            wifiDevice != null -> unlockScreenManager.showUnlockScreen(wifiDevice, request)
            bluetoothDevice != null -> unlockScreenManager.showUnlockScreen(bluetoothDevice, request)
        }
    }
}