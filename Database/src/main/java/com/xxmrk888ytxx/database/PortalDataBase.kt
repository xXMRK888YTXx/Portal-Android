package com.xxmrk888ytxx.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xxmrk888ytxx.database.dao.DeviceDao
import com.xxmrk888ytxx.database.dao.DeviceSettingsDao
import com.xxmrk888ytxx.database.entry.DeviceEntry
import com.xxmrk888ytxx.database.entry.DeviceSettingsEntry

@Database(
    version = 1,
    entities = [DeviceEntry::class, DeviceSettingsEntry::class]
)
abstract class PortalDataBase : RoomDatabase() {
    abstract val deviceDao: DeviceDao
    abstract val deviceSettingsDao: DeviceSettingsDao

    companion object {
        fun createDatabase(context: Context): PortalDataBase {
            return Room.databaseBuilder(context, PortalDataBase::class.java, "database.db")
                .build()
        }
    }
}