package com.xxmrk888ytxx.portal.view.ui

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.portal.domain.model.DeviceType
import com.xxmrk888ytxx.portal.view.model.UnlockScreenUiEvent
import com.xxmrk888ytxx.portal.view.ui.component.ClassicUnlockContent
import com.xxmrk888ytxx.portal.view.ui.component.CompactUnlockModalLandscape
import com.xxmrk888ytxx.portal.view.ui.component.CompactUnlockModalPortrait
import com.xxmrk888ytxx.portal.view.ui.component.FullscreenUnlockContentLandscape
import com.xxmrk888ytxx.portal.view.ui.component.FullscreenUnlockContentPortrait
import com.xxmrk888ytxx.settingsscreen.model.UnlockScreenStyle

@Composable
fun UnlockScreen(
    modifier: Modifier,
    deviceName: String,
    connectionType: DeviceType? = null,
    unlockScreenStyle: UnlockScreenStyle = UnlockScreenStyle.FULLSCREEN,
    onEvent: (UnlockScreenUiEvent) -> Unit
) {
    val activity = LocalActivity.current
    val fragmentActivity = remember(activity) {
        activity as? FragmentActivity ?: error("For this screen should be use FragmentActivity")
    }
    LaunchedEffect(Unit) {
        onEvent(UnlockScreenUiEvent.Allow(fragmentActivity, false))
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    when (unlockScreenStyle) {
        UnlockScreenStyle.CLASSIC -> {
            ClassicUnlockContent(
                modifier = modifier,
                deviceName = deviceName,
                connectionType = connectionType,
                fragmentActivity = fragmentActivity,
                onEvent = onEvent
            )
        }

        UnlockScreenStyle.BOTTOM_SHEET -> {
            if (isLandscape) {
                CompactUnlockModalLandscape(
                    modifier = modifier,
                    deviceName = deviceName,
                    connectionType = connectionType,
                    fragmentActivity = fragmentActivity,
                    onEvent = onEvent
                )
            } else {
                CompactUnlockModalPortrait(
                    modifier = modifier,
                    deviceName = deviceName,
                    connectionType = connectionType,
                    fragmentActivity = fragmentActivity,
                    onEvent = onEvent
                )
            }
        }

        UnlockScreenStyle.FULLSCREEN -> {
            if (isLandscape) {
                FullscreenUnlockContentLandscape(
                    modifier = modifier,
                    deviceName = deviceName,
                    connectionType = connectionType,
                    fragmentActivity = fragmentActivity,
                    onEvent = onEvent
                )
            } else {
                FullscreenUnlockContentPortrait(
                    modifier = modifier,
                    deviceName = deviceName,
                    connectionType = connectionType,
                    fragmentActivity = fragmentActivity,
                    onEvent = onEvent
                )
            }
        }
    }
}
