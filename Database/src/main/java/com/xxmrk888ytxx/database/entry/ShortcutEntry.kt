package com.xxmrk888ytxx.database.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.xxmrk888ytxx.database.entry.ShortcutEntry.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    indices = [Index("shortcutId", unique = true)],
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
data class ShortcutEntry(
    @PrimaryKey @ColumnInfo("shortcutId") val shortcutId: String,
    @ColumnInfo("deviceId") val deviceId: String,
    @ColumnInfo("isRequiredBiometricUnlock") val isRequiredBiometricUnlock: Boolean,
    @ColumnInfo("isSendWOLRequest") val isSendWOLRequest: Boolean
) {
    companion object {
        const val TABLE_NAME = "ShortcutEntry"
    }
}
