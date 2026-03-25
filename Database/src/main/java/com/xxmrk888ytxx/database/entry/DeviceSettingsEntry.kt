package com.xxmrk888ytxx.database.entry

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.xxmrk888ytxx.database.entry.DeviceSettingsEntry.Companion.TABLE_NAME
import com.xxmrk888ytxx.database.model.UnlockMethod

@Entity(
    tableName = TABLE_NAME,
    indices = [Index("deviceId",unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = DeviceEntry::class,
            parentColumns = ["deviceId"],
            childColumns = ["deviceId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class DeviceSettingsEntry(
    @PrimaryKey val deviceId: String,
    val awaitUnlockRequests: Boolean = true,
    val searchIpDynamically: Boolean = false,
    val unlockMethod: UnlockMethod = UnlockMethod.NOTIFICATION,
    val unlockOnlyWhenScreenUnlocked: Boolean = false
) {
    internal companion object {
        const val TABLE_NAME = "DeviceSettingsTable"
    }
}
