package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.ProvideSavedDevices
import com.xxmrk888ytxx.mainscreen.model.DeviceType
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import com.xxmrk888ytxx.mainscreen.model.Device as MainScreenDevice

class ProvideSavedDevicesImpl @Inject constructor(
    wifiDeviceRepository: WifiDeviceRepository,
    bluetoothDeviceRepository: BluetoothDeviceRepository
) : ProvideSavedDevices {
    override val devices: Flow<ImmutableList<MainScreenDevice>> = combine(
        wifiDeviceRepository.devices,
        bluetoothDeviceRepository.devices
    ) { wifiDevices, bluetoothDevices ->

        val mappedWifi = wifiDevices.map { device ->
            MainScreenDevice(
                deviceId = device.deviceId,
                host = device.host,
                deviceName = device.deviceName,
                deviceType = DeviceType.WIFI
            )
        }

        val mappedBluetooth = bluetoothDevices.map { device ->
            MainScreenDevice(
                deviceId = device.clientId,
                host = device.macAddress,
                deviceName = device.name,
                deviceType = DeviceType.BLUETOOTH
            )
        }

        (mappedWifi + mappedBluetooth).toImmutableList()
    }
}