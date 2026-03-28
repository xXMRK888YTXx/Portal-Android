package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.database.dao.BluetoothDeviceDao
import com.xxmrk888ytxx.database.dao.DeviceDao
import com.xxmrk888ytxx.database.entry.BluetoothDeviceEntry
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.WifiDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BluetoothDeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao,
    private val bluetoothDeviceDao: BluetoothDeviceDao
) : BluetoothDeviceRepository {
    override val devices: Flow<List<BluetoothDevice>> = bluetoothDeviceDao.devices.map { list ->
        list.map { it.toDomainModel() }
    }

    override suspend fun saveDevice(bluetoothDevice: BluetoothDevice) = withContext(Dispatchers.IO) {
        deviceDao.upsertBluetoothDevice(
            BluetoothDeviceEntry(
                deviceId = bluetoothDevice.clientId,
                macAddress = bluetoothDevice.macAddress,
                deviceName = bluetoothDevice.name
            )
        )
    }

    override fun getDeviceById(deviceId: String): Flow<BluetoothDevice?> = bluetoothDeviceDao.getWifiDeviceById(deviceId).map { it?.toDomainModel() }

    override suspend fun updateDeviceName(deviceId: String, newName: String) = withContext(Dispatchers.IO) {
        bluetoothDeviceDao.updateDeviceName(deviceId, newName)
    }

    private fun BluetoothDeviceEntry.toDomainModel() : BluetoothDevice {
        return BluetoothDevice(
            clientId = deviceId,
            name = deviceName,
            macAddress = macAddress
        )
    }
}