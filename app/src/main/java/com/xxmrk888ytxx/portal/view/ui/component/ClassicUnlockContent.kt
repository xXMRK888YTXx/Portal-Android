package com.xxmrk888ytxx.portal.view.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.model.DeviceType
import com.xxmrk888ytxx.portal.view.model.UnlockScreenUiEvent

/**
 * Classic Fullscreen Unlock (Standard Theme as it was before changes).
 */
@Composable
fun ClassicUnlockContent(
    modifier: Modifier,
    deviceName: String,
    connectionType: DeviceType?,
    fragmentActivity: FragmentActivity,
    onEvent: (UnlockScreenUiEvent) -> Unit
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Section: Status & Security Protocol Badge
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UnlockHeaderBadge()

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.confirm_your_identity),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Push center content noticeably higher
            Spacer(modifier = Modifier.weight(0.55f))

            // 2. Center Content: Hero Icon, Titles & Prominent Device Name in Center
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UnlockLockIcon(
                    containerSize = 64.dp,
                    iconSize = 32.dp,
                    cornerRadius = 18.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.unlock_request),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.unlock_request_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Prominent Device Name right in the center
                Text(
                    text = deviceName.ifBlank { stringResource(R.string.app_name) },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 34.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Unlock Type Badge (Wi-Fi or Bluetooth)
                UnlockDeviceBadge(connectionType = connectionType)
            }

            // Lower spacer with greater weight to keep center block positioned higher
            Spacer(modifier = Modifier.weight(1.35f))

            // 3. Bottom Action Controls
            UnlockActionButtonsColumn(
                fragmentActivity = fragmentActivity,
                onEvent = onEvent,
                modifier = Modifier.padding(bottom = 6.dp),
                size = UnlockButtonSize.DEFAULT,
                spacing = 10.dp
            )
        }
    }
}
