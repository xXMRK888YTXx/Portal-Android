package com.xxmrk888ytxx.database.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.xxmrk888ytxx.database.entry.BluetoothDeviceEntry.Companion.TABLE_NAME

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
data class BluetoothDeviceEntry(
    @ColumnInfo("deviceId") @PrimaryKey val deviceId: String,
    @ColumnInfo("deviceName") val deviceName: String,
    @ColumnInfo("macAddress") val macAddress: String,
) {
    companion object {
        const val TABLE_NAME = "BluetoothDeviceTable"
    }
}
