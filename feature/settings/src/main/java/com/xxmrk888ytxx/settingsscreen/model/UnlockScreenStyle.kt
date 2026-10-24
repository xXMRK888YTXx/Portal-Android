package com.xxmrk888ytxx.settingsscreen.model

import androidx.annotation.StringRes
import com.xxmrk888ytxx.settingsscreen.R

enum class UnlockScreenStyle(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int
) {
    CLASSIC(
        id = "classic",
        titleRes = R.string.unlock_screen_style_classic,
        descRes = R.string.unlock_screen_style_classic_desc
    ),
    FULLSCREEN(
        id = "fullscreen",
        titleRes = R.string.unlock_screen_style_fullscreen,
        descRes = R.string.unlock_screen_style_fullscreen_desc
    ),
    BOTTOM_SHEET(
        id = "bottom_sheet",
        titleRes = R.string.unlock_screen_style_compact,
        descRes = R.string.unlock_screen_style_compact_desc
    );

    companion object {
        fun fromId(id: String): UnlockScreenStyle =
            entries.find { it.id == id } ?: CLASSIC
    }
}
