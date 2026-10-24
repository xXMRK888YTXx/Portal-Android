package com.xxmrk888ytxx.deviceconfigurationscreen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.deviceconfigurationscreen.R
import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod
import com.xxmrk888ytxx.corecompose.R as ComposeR

@Composable
fun UnlockScenarioCard(
    currentMethod: UnlockMethod,
    isUnsafeUnlockMethodsDisabled: Boolean,
    onMethodChanged: (UnlockMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Option 1: Confirmation Screen
        UnlockScenarioOption(
            icon = ComposeR.drawable.smartphone,
            title = stringResource(R.string.confirmation_screen),
            subtitle = stringResource(R.string.confirmation_screen_desc),
            isSelected = currentMethod is UnlockMethod.ConfirmationScreen,
            enabled = true,
            onClick = { onMethodChanged(UnlockMethod.ConfirmationScreen) }
        )

        // Option 2: Interactive Notification
        val isNotificationEnabled =
            !isUnsafeUnlockMethodsDisabled || !UnlockMethod.Notification.isUnsafe
        UnlockScenarioOption(
            icon = ComposeR.drawable.notifications_active,
            title = stringResource(R.string.notification),
            subtitle = stringResource(R.string.interactive_notification_desc),
            isSelected = currentMethod is UnlockMethod.Notification,
            enabled = isNotificationEnabled,
            onClick = { onMethodChanged(UnlockMethod.Notification) }
        )

        // Option 3: Automatic
        val isAutoEnabled = !isUnsafeUnlockMethodsDisabled || !UnlockMethod.Automatic.isUnsafe
        UnlockScenarioOption(
            icon = ComposeR.drawable.bolt,
            title = stringResource(R.string.automatically),
            subtitle = stringResource(R.string.auto_unlock_desc),
            isSelected = currentMethod is UnlockMethod.Automatic,
            enabled = isAutoEnabled,
            onClick = { onMethodChanged(UnlockMethod.Automatic) }
        )
    }
}
