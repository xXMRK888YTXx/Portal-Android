package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.ProvideSavedDevices
import com.xxmrk888ytxx.mainscreen.model.DeviceType
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import com.xxmrk888ytxx.mainscreen.model.Device as MainScreenDevice

class ProvideSavedDevicesImpl @Inject constructor(
    wifiDeviceRepository: WifiDeviceRepository,
    bluetoothDeviceRepository: BluetoothDeviceRepository,
    bluetoothManager: BluetoothManager
) : ProvideSavedDevices {
    override val devices: Flow<ImmutableList<MainScreenDevice>> = combine(
        wifiDeviceRepository.devices,
        bluetoothDeviceRepository.devices,
        bluetoothManager.pairedDeviceMacAddresses
    ) { wifiDevices, bluetoothDevices, pairedDeviceMacAddresses ->

        val mappedWifi = wifiDevices.map { device ->
            MainScreenDevice(
                clientId = device.clientId,
                host = device.host,
                deviceName = device.deviceName,
                deviceType = DeviceType.WIFI,
                isHaveErrorsWithDevice = false,
                isWakeUpOnLanAvailable = device.wolMacAddress != null
            )
        }

        val mappedBluetooth = bluetoothDevices.map { device ->
            val isPaired = pairedDeviceMacAddresses?.contains(device.macAddress) ?: true
            // If pairedDeviceMacAddresses?.contains(device.macAddress) == null it means permission not granted
            MainScreenDevice(
                clientId = device.clientId,
                host = device.macAddress,
                deviceName = device.name,
                deviceType = DeviceType.BLUETOOTH,
                isHaveErrorsWithDevice = !isPaired,
                isWakeUpOnLanAvailable = false
            )
        }

        (mappedWifi + mappedBluetooth).toImmutableList()
    }
}