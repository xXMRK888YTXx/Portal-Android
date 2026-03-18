package com.xxmrk888ytxx.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.xxmrk888ytxx.database.entry.DeviceEntry
import com.xxmrk888ytxx.database.entry.WifiDeviceEntry

@Dao
abstract class WifiDeviceDao {
    @Query("UPDATE ${WifiDeviceEntry.TABLE_NAME} SET host = :newHost WHERE deviceId = :deviceId")
    abstract suspend fun updateHost(deviceId: String, newHost: String)
}