package com.xxmrk888ytxx.portal.data.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.model.Shortcut
import com.xxmrk888ytxx.portal.utils.getParsableExtraCompat

class ShortcutPinnedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SHORTCUT_PINNED) return
        val shortcut = intent.getParsableExtraCompat(SHORTCUT_DATA_ID_EXTRA, Shortcut::class.java)
        fastDebugLog(shortcut)

    }

    companion object {
        const val ACTION_SHORTCUT_PINNED = "com.xxmrk888ytxx.portal.ACTION_SHORTCUT_PINNED"
        const val SHORTCUT_DATA_ID_EXTRA = "shortcutId"
    }
}