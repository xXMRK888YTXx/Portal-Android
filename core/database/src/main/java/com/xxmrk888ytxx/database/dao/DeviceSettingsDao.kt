package com.xxmrk888ytxx.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.xxmrk888ytxx.database.entry.DeviceSettingsEntry
import com.xxmrk888ytxx.database.model.UnlockMethod.Companion.AUTOMATIC_METHOD_ID
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceSettingsDao {

    @get:Query("SELECT * FROM ${DeviceSettingsEntry.TABLE_NAME}")
    val deviceSettings: Flow<List<DeviceSettingsEntry>>

    @Query("SELECT * FROM ${DeviceSettingsEntry.TABLE_NAME} WHERE clientId = :deviceId LIMIT 1")
    fun getDeviceSettingsByDeviceId(deviceId: String): Flow<DeviceSettingsEntry?>

    @Query("UPDATE ${DeviceSettingsEntry.TABLE_NAME} SET awaitUnlockRequests = :awaitUnlockRequests WHERE clientId = :deviceId")
    suspend fun updateAwaitUnlockRequests(deviceId: String, awaitUnlockRequests: Boolean)

    @Query("UPDATE ${DeviceSettingsEntry.TABLE_NAME} SET searchIpDynamically = :searchIpDynamically WHERE clientId = :deviceId")
    suspend fun updateSearchIpDynamically(deviceId: String, searchIpDynamically: Boolean)

    @Query("UPDATE ${DeviceSettingsEntry.TABLE_NAME} SET unlockMethod = :methodId WHERE clientId = :deviceId")
    suspend fun updateUnlockMethod(deviceId: String, methodId: Int)

    @Query("UPDATE ${DeviceSettingsEntry.TABLE_NAME} SET unlockOnlyWhenScreenUnlocked = :newValue WHERE clientId = :deviceId")
    suspend fun updateUnlockOnlyWhenScreenUnlockedState(deviceId: String, newValue: Boolean)

    @Query("UPDATE ${DeviceSettingsEntry.TABLE_NAME} SET forwardUnlockRequestsToWear = :newValue WHERE clientId = :deviceId")
    suspend fun updateForwardUnlockRequestsToWearState(deviceId: String, newValue: Boolean)

    @Query("SELECT * FROM ${DeviceSettingsEntry.TABLE_NAME} WHERE unlockMethod == $AUTOMATIC_METHOD_ID")
    suspend fun getAllDevicesWithNotSecureUnlockMethod(): List<DeviceSettingsEntry>
}
