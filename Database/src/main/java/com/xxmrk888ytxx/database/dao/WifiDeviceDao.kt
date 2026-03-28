package com.xxmrk888ytxx.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.xxmrk888ytxx.database.entry.WifiDeviceEntry
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WifiDeviceDao {

    @get:Query("SELECT * FROM ${WifiDeviceEntry.TABLE_NAME}")
    abstract val devices: Flow<List<WifiDeviceEntry>>

    @Query("UPDATE ${WifiDeviceEntry.TABLE_NAME} SET host = :newHost WHERE deviceId = :deviceId")
    abstract suspend fun updateHost(deviceId: String, newHost: String)

    @Query("UPDATE ${WifiDeviceEntry.TABLE_NAME} SET deviceName = :newDeviceName WHERE deviceId = :deviceId")
    abstract suspend fun updateDeviceName(deviceId: String, newDeviceName: String)

    @Query("SELECT * FROM ${WifiDeviceEntry.TABLE_NAME} WHERE deviceId = :deviceId LIMIT 1")
    abstract fun getWifiDeviceById(deviceId: String): Flow<WifiDeviceEntry?>
}