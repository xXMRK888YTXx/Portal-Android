package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.data.model.Shortcut

interface ShortcutRepository {
    suspend fun registerShortcut(shortcut: Shortcut)
    suspend fun removeShortcut(shortcutId: String)
    suspend fun getShortcutById(shortcutId: String): Shortcut?
    suspend fun getShortcutsByDeviceId(deviceId: String): List<Shortcut>
}