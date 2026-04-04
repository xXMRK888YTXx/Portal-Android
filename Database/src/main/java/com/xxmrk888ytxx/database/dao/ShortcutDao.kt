package com.xxmrk888ytxx.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.xxmrk888ytxx.database.entry.ShortcutEntry

@Dao
interface ShortcutDao {
    @Upsert
    suspend fun addShortcut(shortcut: ShortcutEntry)

    @Query("DELETE FROM ${ShortcutEntry.TABLE_NAME} WHERE shortcutId = :shortcutId")
    suspend fun removeShortcut(shortcutId: String)

    @Query("SELECT * FROM ${ShortcutEntry.TABLE_NAME} WHERE shortcutId = :shortcutId LIMIT 1")
    suspend fun getShortcut(shortcutId: String): ShortcutEntry?

    @Query("SELECT * FROM ${ShortcutEntry.TABLE_NAME} WHERE deviceId = :deviceId")
    suspend fun getShortcutsByDeviceId(deviceId: String): List<ShortcutEntry>

    @Query("SELECT * FROM ${ShortcutEntry.TABLE_NAME} WHERE isRequiredBiometricUnlock = 0")
    suspend fun getShortcutWithInsecureUnlock(): List<ShortcutEntry>

    @Query("UPDATE ${ShortcutEntry.TABLE_NAME} SET isRequiredBiometricUnlock = :newValue WHERE shortcutId = :shortcutId")
    suspend fun updateIsRequiredBiometricUnlock(shortcutId: String, newValue: Boolean)
}