package com.xxmrk888ytxx.portal.data.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.model.Shortcut
import com.xxmrk888ytxx.portal.domain.ShortcutRepository
import com.xxmrk888ytxx.portal.utils.getParsableExtraCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShortcutPinnedReceiver @Inject constructor(
    private val shortcutRepository: ShortcutRepository
) : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SHORTCUT_PINNED) return
        val shortcut = intent.getParsableExtraCompat(SHORTCUT_DATA_ID_EXTRA, Shortcut::class.java) ?: return
        fastDebugLog("Register shortcut $shortcut")
        scope.launch { shortcutRepository.registerShortcut(shortcut) }.invokeOnCompletion { scope.cancel() }
    }

    companion object {
        const val ACTION_SHORTCUT_PINNED = "com.xxmrk888ytxx.portal.ACTION_SHORTCUT_PINNED"
        const val SHORTCUT_DATA_ID_EXTRA = "shortcutId"
    }
}