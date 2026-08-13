package com.xxmrk888ytxx.database.typeConverter

import androidx.room3.ColumnTypeConverter
import com.xxmrk888ytxx.database.model.UnlockMethod


class UnlockMethodConverter {

    @ColumnTypeConverter
    fun fromUnlockMethod(value: UnlockMethod): Int {
        return value.id
    }

    @ColumnTypeConverter
    fun toUnlockMethod(value: Int): UnlockMethod =
        UnlockMethod.entries.firstOrNull { it.id == value } ?: UnlockMethod.NOTIFICATION
}