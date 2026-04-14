package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.data.model.Shortcut

interface ShortcutManager {
    suspend fun addShortcut(shortcut: Shortcut, label: String)
    suspend fun removeShortcut(shortcutId: String)
    suspend fun isLauncherCanToCreateShortcut(): Boolean
}