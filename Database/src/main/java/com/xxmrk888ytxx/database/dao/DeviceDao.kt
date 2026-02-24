package com.xxmrk888ytxx.database.dao

import androidx.annotation.RestrictTo
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.xxmrk888ytxx.database.entry.DeviceEntry
import com.xxmrk888ytxx.database.entry.DeviceSettingsEntry
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DeviceDao {
    @get:Query("SELECT * FROM ${DeviceEntry.TABLE_NAME}")
    abstract val devices: Flow<List<DeviceEntry>>

    @Query("SELECT * FROM ${DeviceEntry.TABLE_NAME} WHERE deviceId = :deviceId LIMIT 1")
    abstract fun getDeviceById(deviceId: String): Flow<DeviceEntry?>

    @Upsert
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    protected abstract suspend fun upsertDeviceInternal(deviceEntry: DeviceEntry)

    @Upsert
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    abstract suspend fun upsertDeviceSettingsInternal(deviceEntry: DeviceSettingsEntry)

    @Transaction
    open suspend fun upsertDevice(deviceEntry: DeviceEntry) {
        upsertDeviceInternal(deviceEntry)
        //Add default device settings
        upsertDeviceSettingsInternal(DeviceSettingsEntry(
            deviceId = deviceEntry.deviceId,
            awaitUnlockRequests = true
        ))
    }

    @Query("DELETE FROM ${DeviceEntry.TABLE_NAME} WHERE deviceId = :deviceId")
    abstract suspend fun removeDevice(deviceId: String)
}