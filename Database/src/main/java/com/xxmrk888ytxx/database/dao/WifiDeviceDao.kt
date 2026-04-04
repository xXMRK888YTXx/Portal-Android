package com.xxmrk888ytxx.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.xxmrk888ytxx.database.entry.WifiDeviceEntry
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WifiDeviceDao {

    @get:Query("SELECT * FROM ${WifiDeviceEntry.TABLE_NAME}")
    abstract val devices: Flow<List<WifiDeviceEntry>>

    @Query("UPDATE ${WifiDeviceEntry.TABLE_NAME} SET host = :newHost WHERE clientId = :deviceId")
    abstract suspend fun updateHost(deviceId: String, newHost: String)

    @Query("UPDATE ${WifiDeviceEntry.TABLE_NAME} SET deviceName = :newDeviceName WHERE clientId = :deviceId")
    abstract suspend fun updateDeviceName(deviceId: String, newDeviceName: String)

    @Query("SELECT * FROM ${WifiDeviceEntry.TABLE_NAME} WHERE clientId = :deviceId LIMIT 1")
    abstract fun getWifiDeviceById(deviceId: String): Flow<WifiDeviceEntry?>

    @Query("UPDATE ${WifiDeviceEntry.TABLE_NAME} SET wolMacAddress = :macAddress WHERE clientId = :deviceId")
    abstract suspend fun updateWOLMacAddress(deviceId: String, macAddress: String)
}