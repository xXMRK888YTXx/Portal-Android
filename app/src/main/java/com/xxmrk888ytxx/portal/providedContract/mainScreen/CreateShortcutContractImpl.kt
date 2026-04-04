package com.xxmrk888ytxx.portal.providedContract.mainScreen

import android.content.Context
import com.xxmrk888ytxx.mainscreen.contract.CreateShortcutContract
import com.xxmrk888ytxx.mainscreen.exception.LauncherNotSupportShortcutException
import com.xxmrk888ytxx.mainscreen.model.ShortcutOption
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.data.model.Shortcut
import com.xxmrk888ytxx.portal.domain.ShortcutManager
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