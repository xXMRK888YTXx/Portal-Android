package com.xxmrk888ytxx.database.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.xxmrk888ytxx.database.entry.DeviceEntry.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    indices = [Index("clientId", unique = true)]
)
data class DeviceEntry(
    @ColumnInfo("clientId") @PrimaryKey val clientId: String,
) {
    internal companion object {
        const val TABLE_NAME = "DeviceTable"
    }
}
