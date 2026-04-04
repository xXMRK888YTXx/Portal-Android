package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.UnlockMessageSender
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.UnlockRequestHandler
import com.xxmrk888ytxx.portal.domain.UnlockRequestManager
import com.xxmrk888ytxx.portal.domain.model.UnlockMethod
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class UnlockRequestHandlerImpl @Inject constructor(
    private val unlockMessageSender: UnlockMessageSender,
    private val unlockRequestManager: UnlockRequestManager,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository,
    private val deviceSettingsRepository: DeviceSettingsRepository
) : UnlockRequestHandler {

    private val _handledRequestsId = MutableStateFlow(emptySet<String>())


    override suspend fun onNewRequest(
        clientId: String,
        request: UnlockServiceRequest
    ) {
        if (request.requestId != null && _handledRequestsId.value.contains(request.requestId)) {
            fastDebugLog("${request.requestId} Already Handled. Skip")
            return
        }
        request.requestId?.let { requestId ->
            _handledRequestsId.update { it + requestId }
        }
        fastDebugLog("onNewRequest: $request")
        val wifiDevice = wifiDeviceRepository.getDeviceById(clientId).first()
        val bluetoothDevice = bluetoothDeviceRepository.getDeviceById(clientId).first()
        val deviceName = wifiDevice?.deviceName ?: bluetoothDevice?.name ?: return
        val settings = deviceSettingsRepository.getDeviceSettingsByDeviceId(clientId).first() ?: return

        when(settings.unlockMethod) {
            UnlockMethod.Automatic -> unlockRequestManager.automaticUnlock(
                clientId = clientId,
                unlockOnlyWhenScreenUnlocked = settings.unlockOnlyWhenScreenUnlocked,
                request = request
            )
            UnlockMethod.ConfirmationScreen -> unlockRequestManager.showUnlockScreen(
                clientId = clientId,
                deviceName = deviceName,
                request = request
            )
            UnlockMethod.Notification -> unlockRequestManager.sendNotification(
                clientId = clientId,
                deviceName = deviceName,
                request = request
            )
        }
    }
}