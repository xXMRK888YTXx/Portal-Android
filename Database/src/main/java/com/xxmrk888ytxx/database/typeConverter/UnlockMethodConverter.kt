package com.xxmrk888ytxx.database.typeConverter

import androidx.room.TypeConverter
import com.xxmrk888ytxx.database.model.UnlockMethod


class UnlockMethodConverter {

    @TypeConverter
    fun fromUnlockMethod(value: UnlockMethod): Int {
        return value.id
    }

    @TypeConverter
    fun toUnlockMethod(value: Int): UnlockMethod =
        UnlockMethod.entries.firstOrNull { it.id == value } ?: UnlockMethod.NOTIFICATION
}