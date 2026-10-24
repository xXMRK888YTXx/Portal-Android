package com.xxmrk888ytxx.deviceconfigurationscreen.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.deviceconfigurationscreen.R
import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device
import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod
import com.xxmrk888ytxx.corecompose.R as ComposeR

@Composable
fun AuthorizationParametersCard(
    device: Device,
    onUnlockOnlyWhenUnlockedChanged: (Boolean) -> Unit,
    onAwaitUnlockChanged: (Boolean) -> Unit,
    onForwardToWearChanged: (Boolean) -> Unit,
    onSearchIpDynamicallyChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // Toggle 1: Only Unlocked Phone
            val (title, description) = if (device.unlockMethod is UnlockMethod.Automatic) {
                Pair(
                    stringResource(R.string.unlock_only_when_unlocked),
                    stringResource(R.string.unlock_only_when_unlocked_description)
                )
            } else {
                Pair(
                    stringResource(R.string.show_unlock_request_only_when_unlocked),
                    stringResource(R.string.show_unlock_request_only_when_unlocked_description)
                )
            }

            SwitchItemRow(
                icon = ComposeR.drawable.fingerprint,
                title = title,
                description = description,
                isChecked = device.showUnlockScreenOrUnlockOnlyWhenScreenUnlocked,
                onCheckedChange = onUnlockOnlyWhenUnlockedChanged
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            // Toggle 2: Await Unlock Requests
            SwitchItemRow(
                icon = ComposeR.drawable.memory,
                title = stringResource(R.string.await_unlock_requests),
                description = stringResource(R.string.await_unlock_requests_description),
                isChecked = device.awaitUnlockRequests,
                onCheckedChange = onAwaitUnlockChanged
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            // Toggle 3: Sync with Wear OS
            SwitchItemRow(
                icon = ComposeR.drawable.watch,
                title = stringResource(R.string.forward_unlock_requests_to_wear),
                description = stringResource(R.string.forward_unlock_requests_to_wear_description),
                isChecked = device.forwardUnlockRequestsToWear,
                onCheckedChange = onForwardToWearChanged
            )

            // Toggle 4: Dynamic IP mDNS (for WifiDevice)
            if (device is Device.WifiDevice) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    thickness = 1.dp
                )

                SwitchItemRow(
                    icon = ComposeR.drawable.router,
                    title = stringResource(R.string.search_ip_dynamically),
                    description = stringResource(R.string.search_ip_dynamically_description),
                    isChecked = device.searchIpDynamically,
                    onCheckedChange = onSearchIpDynamicallyChanged
                )
            }
        }
    }
}
