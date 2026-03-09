package com.xxmrk888ytxx.portal.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.data.broadcastReceiver.ShortcutPinnedReceiver
import com.xxmrk888ytxx.portal.data.broadcastReceiver.ShortcutPinnedReceiver.Companion.SHORTCUT_DATA_ID_EXTRA
import com.xxmrk888ytxx.portal.data.model.Shortcut
import com.xxmrk888ytxx.portal.domain.ShortcutManager
import com.xxmrk888ytxx.portal.domain.ShortcutRepository
import com.xxmrk888ytxx.portal.view.fastUnlockActivity.FastUnlockActivity
import com.xxmrk888ytxx.portal.view.fastUnlockActivity.FastUnlockActivity.Companion.SHORTCUT_ID_EXTRA
import javax.inject.Inject

class ShortcutManagerImpl @Inject constructor(
    private val context: Context,
    private val shortcutRepository: ShortcutRepository
) : ShortcutManager {
    override suspend fun addShortcut(shortcut: Shortcut, label: String) {
        val targetIntent = Intent(context, FastUnlockActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(SHORTCUT_ID_EXTRA, shortcut.shortcutId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val shortcutInfo = ShortcutInfoCompat.Builder(context, shortcut.shortcutId)
            .setShortLabel(label)
            .setIcon(
                IconCompat.createWithResource(
                    context,
                    com.xxmrk888ytxx.mainscreen.R.drawable.lock_open
                )
            )
            .setIntent(targetIntent)
            .build()

        val callbackIntent = Intent(context, ShortcutPinnedReceiver::class.java).apply {
            action = ShortcutPinnedReceiver.ACTION_SHORTCUT_PINNED
            putExtra(SHORTCUT_DATA_ID_EXTRA, shortcut)
        }

        val successCallback = PendingIntent.getBroadcast(
            /* context = */ context,
            /* requestCode = */ 0,
            /* intent = */ callbackIntent,
            /* flags = */ PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        ShortcutManagerCompat.requestPinShortcut(
            context,
            shortcutInfo,
            successCallback.intentSender
        )
    }

    override suspend fun removeShortcut(shortcutId: String) {
        val shortcutsToDisable = listOf(shortcutId)

        val disabledMessage =
            context.getString(R.string.the_device_has_been_removed_the_shortcut_is_not_available)

        ShortcutManagerCompat.disableShortcuts(
            context,
            shortcutsToDisable,
            disabledMessage
        )
        shortcutRepository.removeShortcut(shortcutId)
    }

    override suspend fun isLauncherCanToCreateShortcut(): Boolean = ShortcutManagerCompat.isRequestPinShortcutSupported(context)
}