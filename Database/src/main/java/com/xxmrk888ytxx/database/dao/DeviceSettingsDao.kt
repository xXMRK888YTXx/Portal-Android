package com.xxmrk888ytxx.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.xxmrk888ytxx.database.entry.DeviceSettingsEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceSettingsDao {

    @get:Query("SELECT * FROM ${DeviceSettingsEntry.TABLE_NAME}")
    val deviceSettings: Flow<List<DeviceSettingsEntry>>
    @Query("SELECT * FROM ${DeviceSettingsEntry.TABLE_NAME} WHERE deviceId = :deviceId LIMIT 1")
    fun getDeviceSettingsByDeviceId(deviceId: String): Flow<DeviceSettingsEntry?>

    @Query("UPDATE ${DeviceSettingsEntry.TABLE_NAME} SET awaitUnlockRequests = :awaitUnlockRequests WHERE deviceId = :deviceId")
    suspend fun updateAwaitUnlockRequests(deviceId: String, awaitUnlockRequests: Boolean)

    @Query("UPDATE ${DeviceSettingsEntry.TABLE_NAME} SET searchIpDynamically = :searchIpDynamically WHERE deviceId = :deviceId")
    suspend fun updateSearchIpDynamically(deviceId: String, searchIpDynamically: Boolean)
}