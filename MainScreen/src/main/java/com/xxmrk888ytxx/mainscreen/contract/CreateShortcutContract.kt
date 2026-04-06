package com.xxmrk888ytxx.mainscreen.contract

import com.xxmrk888ytxx.mainscreen.exception.LauncherNotSupportShortcutException
import com.xxmrk888ytxx.mainscreen.model.ShortcutOption

interface CreateShortcutContract {
    @Throws(LauncherNotSupportShortcutException::class)
    suspend fun createShortcutContract(shortcutOption: ShortcutOption): Result<Unit>
}