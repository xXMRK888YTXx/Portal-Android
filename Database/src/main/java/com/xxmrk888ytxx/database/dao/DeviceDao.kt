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

    @Insert
    suspend fun insertDevice(deviceEntry: DeviceEntry)
}