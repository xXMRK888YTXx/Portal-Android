package com.xxmrk888ytxx.database.entry

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.xxmrk888ytxx.database.entry.WifiDeviceEntry.Companion.TABLE_NAME

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
data class WifiDeviceEntry(
    @ColumnInfo("clientId") @PrimaryKey val clientId: String,
    @ColumnInfo("deviceName") val deviceName: String,
    @ColumnInfo("host") val host: String,
    @ColumnInfo("serverCertificateFingerprint") val serverCertificateFingerprint: String,
    @ColumnInfo("clientCertificateKeyAlias") val clientCertificateKeyAlias: String,
    @ColumnInfo("wolMacAddress") val wolMacAddress: String? = null
) {
    companion object {
        const val TABLE_NAME = "WifiDeviceTable"
    }
}