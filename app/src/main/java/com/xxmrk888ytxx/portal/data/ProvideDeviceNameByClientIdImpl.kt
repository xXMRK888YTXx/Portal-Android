package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.ProvideDeviceNameByClientId
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.model.DeviceType
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProvideDeviceNameByClientIdImpl @Inject constructor(
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository
) : ProvideDeviceNameByClientId {
    override suspend fun provideName(clientId: String): String? {
        val wifiDevice = wifiDeviceRepository.getDeviceById(clientId).first()
        if (wifiDevice != null) return wifiDevice.deviceName
        val bluetoothDevice = bluetoothDeviceRepository.getDeviceById(clientId).first()
        return bluetoothDevice?.name
    }

    override suspend fun provideType(clientId: String): DeviceType? {
        val wifiDevice = wifiDeviceRepository.getDeviceById(clientId).first()
        if (wifiDevice != null) return DeviceType.WIFI
        val bluetoothDevice = bluetoothDeviceRepository.getDeviceById(clientId).first()
        if (bluetoothDevice != null) return DeviceType.BLUETOOTH
        return null
    }
}
