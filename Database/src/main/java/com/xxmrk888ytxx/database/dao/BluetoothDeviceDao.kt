package com.xxmrk888ytxx.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.xxmrk888ytxx.database.entry.BluetoothDeviceEntry
import com.xxmrk888ytxx.database.entry.WifiDeviceEntry
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BluetoothDeviceDao {
    @get:Query("SELECT * FROM ${BluetoothDeviceEntry.TABLE_NAME}")
    abstract val devices: Flow<List<BluetoothDeviceEntry>>

    @Query("SELECT * FROM ${BluetoothDeviceEntry.TABLE_NAME} WHERE clientId = :clientId LIMIT 1")
    abstract fun getWifiDeviceById(clientId: String): Flow<BluetoothDeviceEntry?>

    @Query("UPDATE ${BluetoothDeviceEntry.TABLE_NAME} SET deviceName = :newDeviceName WHERE clientId = :clientId")
    abstract suspend fun updateDeviceName(clientId: String, newDeviceName: String)
}