package com.xxmrk888ytxx.database.entry

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.xxmrk888ytxx.database.entry.DeviceSettingsEntry.Companion.TABLE_NAME
import com.xxmrk888ytxx.database.model.UnlockMethod

@Entity(
    tableName = TABLE_NAME,
    indices = [Index("clientId", unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = DeviceEntry::class,
            parentColumns = ["clientId"],
            childColumns = ["clientId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class DeviceSettingsEntry(
    @PrimaryKey @ColumnInfo("clientId") val clientId: String,
    @ColumnInfo("awaitUnlockRequests") val awaitUnlockRequests: Boolean = true,
    @ColumnInfo("searchIpDynamically") val searchIpDynamically: Boolean = false,
    @ColumnInfo("unlockMethod") val unlockMethod: UnlockMethod = UnlockMethod.NOTIFICATION,
    @ColumnInfo("unlockOnlyWhenScreenUnlocked") val unlockOnlyWhenScreenUnlocked: Boolean = false
) {
    internal companion object {
        const val TABLE_NAME = "DeviceSettingsTable"
    }
}
