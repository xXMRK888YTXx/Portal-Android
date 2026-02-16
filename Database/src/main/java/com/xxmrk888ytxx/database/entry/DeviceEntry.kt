package com.xxmrk888ytxx.database.entry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.xxmrk888ytxx.database.entry.DeviceEntry.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    indices = [Index("deviceId")]
)
data class DeviceEntry(
    @ColumnInfo("deviceId") @PrimaryKey val deviceId: String,
    @ColumnInfo("host") val host: String,
    @ColumnInfo("serverCertificateFingerprint") val serverCertificateFingerprint: String,
    @ColumnInfo("clientCertificateKeyAlias") val clientCertificateKeyAlias: String,
) {
    internal companion object {
        const val TABLE_NAME = "DeviceTable"
    }
}
