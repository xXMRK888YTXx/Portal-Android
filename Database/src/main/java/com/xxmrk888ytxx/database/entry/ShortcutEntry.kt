package com.xxmrk888ytxx.database.entry

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.xxmrk888ytxx.database.entry.ShortcutEntry.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    indices = [Index("shortcutId", unique = true)],
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
data class ShortcutEntry(
    @PrimaryKey @ColumnInfo("shortcutId") val shortcutId: String,
    @ColumnInfo("clientId") val clientId: String,
    @ColumnInfo("isRequiredBiometricUnlock") val isRequiredBiometricUnlock: Boolean,
    @ColumnInfo("isSendWOLRequest") val isSendWOLRequest: Boolean
) {
    companion object {
        const val TABLE_NAME = "ShortcutEntry"
    }
}
