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
}