package com.xxmrk888ytxx.database

import android.content.Context
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.xxmrk888ytxx.database.dao.BluetoothDeviceDao
import com.xxmrk888ytxx.database.dao.DeviceDao
import com.xxmrk888ytxx.database.dao.DeviceSettingsDao
import com.xxmrk888ytxx.database.dao.ShortcutDao
import com.xxmrk888ytxx.database.dao.WifiDeviceDao
import com.xxmrk888ytxx.database.entry.BluetoothDeviceEntry
import com.xxmrk888ytxx.database.entry.DeviceEntry
import com.xxmrk888ytxx.database.entry.DeviceSettingsEntry
import com.xxmrk888ytxx.database.entry.ShortcutEntry
import com.xxmrk888ytxx.database.entry.WifiDeviceEntry
import com.xxmrk888ytxx.database.typeConverter.UnlockMethodConverter

@Database(
    version = 2,
    entities = [DeviceEntry::class, WifiDeviceEntry::class, BluetoothDeviceEntry::class, DeviceSettingsEntry::class, ShortcutEntry::class]
)
@ColumnTypeConverters(UnlockMethodConverter::class)
abstract class PortalDataBase : RoomDatabase() {
    abstract val deviceDao: DeviceDao
    abstract val deviceSettingsDao: DeviceSettingsDao
    abstract val shortcutDao: ShortcutDao
    abstract val wifiDeviceDao: WifiDeviceDao
    abstract val bluetoothDeviceDao: BluetoothDeviceDao

    companion object {
        fun createDatabase(context: Context): PortalDataBase {
            return Room.databaseBuilder(context, PortalDataBase::class.java, "database.db")
                .addMigrations(MIGRATION_1_2)
                .build()
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override suspend fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    "ALTER TABLE ${DeviceSettingsEntry.TABLE_NAME} ADD COLUMN forwardUnlockRequestsToWear INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}
