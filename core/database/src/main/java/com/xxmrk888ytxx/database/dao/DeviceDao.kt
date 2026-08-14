package com.xxmrk888ytxx.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.xxmrk888ytxx.database.entry.BluetoothDeviceEntry
import com.xxmrk888ytxx.database.entry.DeviceEntry
import com.xxmrk888ytxx.database.entry.DeviceSettingsEntry
import com.xxmrk888ytxx.database.entry.WifiDeviceEntry
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DeviceDao {

    @get:Query("SELECT * FROM ${DeviceEntry.TABLE_NAME}")
    abstract val devices: Flow<List<DeviceEntry>>

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
        upsertDevice(DeviceEntry(clientId = wifiDeviceEntry.clientId))
        upsertWifiDeviceInternal(wifiDeviceEntry)
    }

    @Transaction
    open suspend fun upsertBluetoothDevice(bluetoothDeviceEntry: BluetoothDeviceEntry) {
        upsertDevice(DeviceEntry(clientId = bluetoothDeviceEntry.clientId))
        upsertBluetoothDeviceInternal(bluetoothDeviceEntry)
    }


    @Transaction
    protected open suspend fun upsertDevice(deviceEntry: DeviceEntry) {
        upsertDeviceInternal(deviceEntry)
        //Add default device settings
        upsertDeviceSettingsInternal(DeviceSettingsEntry(
            clientId = deviceEntry.clientId,
        ))
    }

    @Query("DELETE FROM ${DeviceEntry.TABLE_NAME} WHERE clientId = :clientId")
    abstract suspend fun removeDevice(clientId: String)
}