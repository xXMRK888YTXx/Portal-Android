package com.xxmrk888ytxx.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xxmrk888ytxx.database.entry.DeviceEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @get:Query("SELECT * FROM ${DeviceEntry.TABLE_NAME}")
    val devices: Flow<List<DeviceEntry>>

    @Query("SELECT * FROM ${DeviceEntry.TABLE_NAME} WHERE deviceId = :deviceId LIMIT 1")
    fun getDeviceById(deviceId: String): Flow<DeviceEntry?>

    @Insert
    suspend fun insertDevice(deviceEntry: DeviceEntry)

    @Query("DELETE FROM ${DeviceEntry.TABLE_NAME} WHERE deviceId = :deviceId")
    suspend fun removeDevice(deviceId: String)
}