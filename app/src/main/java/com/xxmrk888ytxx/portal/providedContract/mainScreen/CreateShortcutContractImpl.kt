package com.xxmrk888ytxx.portal.providedContract.mainScreen

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.xxmrk888ytxx.mainscreen.contract.CreateShortcutContract
import com.xxmrk888ytxx.mainscreen.exception.LauncherNotSupportShortcutException
import com.xxmrk888ytxx.mainscreen.model.ShortcutOption
import com.xxmrk888ytxx.portal.data.broadcastReceiver.ShortcutPinnedReceiver
import com.xxmrk888ytxx.portal.data.broadcastReceiver.ShortcutPinnedReceiver.Companion.SHORTCUT_DATA_ID_EXTRA
import com.xxmrk888ytxx.portal.data.model.Shortcut
import com.xxmrk888ytxx.portal.view.fastUnlockActivity.FastUnlockActivity
import com.xxmrk888ytxx.portal.view.fastUnlockActivity.FastUnlockActivity.Companion.SHORTCUT_ID_EXTRA
import java.util.UUID
import javax.inject.Inject

class CreateShortcutContractImpl @Inject constructor(
    private val context: Context,
) : CreateShortcutContract {
    override suspend fun createShortcutContract(shortcutOption: ShortcutOption): Result<Unit> =
        runCatching {
            if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) throw LauncherNotSupportShortcutException()

            val shortcut = Shortcut(
                shortcutId = UUID.randomUUID().toString(),
                clientId = shortcutOption.device.deviceId,
                isRequiredBiometricUnlock = shortcutOption.isRequiredBiometricUnlock
            )
            val targetIntent = Intent(context, FastUnlockActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra(SHORTCUT_ID_EXTRA, shortcut.shortcutId)
            }

            val shortcutInfo = ShortcutInfoCompat.Builder(context, shortcutOption.device.deviceId)
                .setShortLabel("Unlock ${shortcutOption.device.deviceName}")
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
}