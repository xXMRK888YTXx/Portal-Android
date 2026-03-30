package com.xxmrk888ytxx.database.entry

import androidx.room.ColumnInfo
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
    @PrimaryKey @ColumnInfo("deviceId") val deviceId: String,
    @ColumnInfo("awaitUnlockRequests") val awaitUnlockRequests: Boolean = true,
    @ColumnInfo("searchIpDynamically") val searchIpDynamically: Boolean = false,
    @ColumnInfo("unlockMethod") val unlockMethod: UnlockMethod = UnlockMethod.NOTIFICATION,
    @ColumnInfo("unlockOnlyWhenScreenUnlocked") val unlockOnlyWhenScreenUnlocked: Boolean = false
) {
    internal companion object {
        const val TABLE_NAME = "DeviceSettingsTable"
    }
}
