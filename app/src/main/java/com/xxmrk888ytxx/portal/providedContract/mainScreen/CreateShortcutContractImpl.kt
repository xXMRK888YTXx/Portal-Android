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
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.data.broadcastReceiver.ShortcutPinnedReceiver
import com.xxmrk888ytxx.portal.data.broadcastReceiver.ShortcutPinnedReceiver.Companion.SHORTCUT_DATA_ID_EXTRA
import com.xxmrk888ytxx.portal.data.model.Shortcut
import com.xxmrk888ytxx.portal.domain.ShortcutManager
import com.xxmrk888ytxx.portal.view.fastUnlockActivity.FastUnlockActivity
import com.xxmrk888ytxx.portal.view.fastUnlockActivity.FastUnlockActivity.Companion.SHORTCUT_ID_EXTRA
import java.util.UUID
import javax.inject.Inject

class CreateShortcutContractImpl @Inject constructor(
    private val shortcutManager: ShortcutManager,
    private val context: Context
) : CreateShortcutContract {
    override suspend fun createShortcutContract(shortcutOption: ShortcutOption): Result<Unit> =
        runCatching {
            if (!shortcutManager.isLauncherCanToCreateShortcut()) throw LauncherNotSupportShortcutException()

            val shortcut = Shortcut(
                shortcutId = UUID.randomUUID().toString(),
                clientId = shortcutOption.device.deviceId,
                isRequiredBiometricUnlock = shortcutOption.isRequiredBiometricUnlock,
                isSendWOLRequest = shortcutOption.isSendWOLRequest
            )
            shortcutManager.addShortcut(shortcut,
                context.getString(R.string.unlock, shortcutOption.device.deviceName))
        }
}