package com.xxmrk888ytxx.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
    version = 1,
    entities = [DeviceEntry::class, WifiDeviceEntry::class, BluetoothDeviceEntry::class, DeviceSettingsEntry::class, ShortcutEntry::class]
)
@TypeConverters(UnlockMethodConverter::class)
abstract class PortalDataBase : RoomDatabase() {
    abstract val deviceDao: DeviceDao
    abstract val deviceSettingsDao: DeviceSettingsDao
    abstract val shortcutDao: ShortcutDao
    abstract val wifiDeviceDao: WifiDeviceDao
    abstract val bluetoothDeviceDao: BluetoothDeviceDao

    companion object {
        fun createDatabase(context: Context): PortalDataBase {
            return Room.databaseBuilder(context, PortalDataBase::class.java, "database.db")
                .build()
        }
    }
}