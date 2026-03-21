package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.di.qualifier.BluetoothUnlockServiceManagerQualifier
import com.xxmrk888ytxx.portal.di.qualifier.WifiUnlockServiceManagerQualifier
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.UnlockServiceManager
import com.xxmrk888ytxx.portal.domain.UnlockMessageSender
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UnlockMessageSenderImpl @Inject constructor(
    @param:WifiUnlockServiceManagerQualifier private val wifiUnlockServiceManagerQualifier: UnlockServiceManager,
    @param:BluetoothUnlockServiceManagerQualifier private val bluetoothUnlockServiceManager: UnlockServiceManager,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository
) : UnlockMessageSender {
    override suspend fun sendMessage(
        clientId: String,
        message: UnlockServiceMessage
    ): Result<Unit> = runCatching(Dispatchers.IO) {
        val unlockServiceManager = when {
            wifiDeviceRepository.getDeviceById(clientId).first() != null -> wifiUnlockServiceManagerQualifier
            bluetoothDeviceRepository.getDeviceById(clientId).first() != null -> bluetoothUnlockServiceManager
            else -> throw IllegalArgumentException("Device with id $clientId not found")
        }
        unlockServiceManager.sendMessageToHost(clientId, message).getOrThrow()
    }
}