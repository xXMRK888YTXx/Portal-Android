package com.xxmrk888ytxx.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.xxmrk888ytxx.database.entry.BluetoothDeviceEntry
import com.xxmrk888ytxx.database.entry.DeviceEntry
import com.xxmrk888ytxx.database.entry.DeviceSettingsEntry
import com.xxmrk888ytxx.database.entry.WifiDeviceEntry
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DeviceDao {

    @Upsert
    protected abstract suspend fun upsertDeviceInternal(deviceEntry: DeviceEntry)

    @Upsert
    protected abstract suspend fun upsertWifiDeviceInternal(deviceEntry: WifiDeviceEntry)

    @Upsert
    protected abstract suspend fun upsertBluetoothDeviceInternal(deviceEntry: BluetoothDeviceEntry)

    @Upsert
    protected abstract suspend fun upsertDeviceSettingsInternal(deviceEntry: DeviceSettingsEntry)

    @Transaction
    open suspend fun upsertWifiDevice(wifiDeviceEntry: WifiDeviceEntry) {
        upsertDevice(DeviceEntry(deviceId = wifiDeviceEntry.deviceId))
        upsertWifiDeviceInternal(wifiDeviceEntry)
    }

    @Transaction
    open suspend fun upsertBluetoothDevice(bluetoothDeviceEntry: BluetoothDeviceEntry) {
        upsertDevice(DeviceEntry(deviceId = bluetoothDeviceEntry.deviceId))
        upsertBluetoothDeviceInternal(bluetoothDeviceEntry)
    }


    @Transaction
    protected open suspend fun upsertDevice(deviceEntry: DeviceEntry) {
        upsertDeviceInternal(deviceEntry)
        //Add default device settings
        upsertDeviceSettingsInternal(DeviceSettingsEntry(
            deviceId = deviceEntry.deviceId,
        ))
    }

    @Query("DELETE FROM ${DeviceEntry.TABLE_NAME} WHERE deviceId = :deviceId")
    abstract suspend fun removeDevice(deviceId: String)
}