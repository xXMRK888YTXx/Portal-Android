package com.xxmrk888ytxx.database.entry

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.xxmrk888ytxx.database.entry.BluetoothDeviceEntry.Companion.TABLE_NAME

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
data class BluetoothDeviceEntry(
    @ColumnInfo("clientId") @PrimaryKey val clientId: String,
    @ColumnInfo("deviceName") val deviceName: String,
    @ColumnInfo("macAddress") val macAddress: String,
) {
    companion object {
        const val TABLE_NAME = "BluetoothDeviceTable"
    }
}
