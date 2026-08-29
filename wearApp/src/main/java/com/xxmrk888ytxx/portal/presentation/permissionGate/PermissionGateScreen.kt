package com.xxmrk888ytxx.portal.presentation.permissionGate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.presentation.mainActivity.MainActivityEvent
import com.xxmrk888ytxx.portal.presentation.mainActivity.MainScreenState

/**
 * Modern, user-friendly permission gate screen for Wear OS.
 *
 * Prompts user with clear context and a direct call-to-action button to grant notification permission.
 */
@Composable
fun PermissionGateScreen(
    state: MainScreenState,
    onEvent: (MainActivityEvent) -> Unit
) {
    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .transformedHeight(this, transformationSpec),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notifications),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.permission_notifications_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .transformedHeight(this, transformationSpec)
                )
            }

            item {
                Spacer(Modifier.height(6.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.permission_notifications_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .transformedHeight(this, transformationSpec)
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
            }

            item {
                Button(
                    onClick = { onEvent(MainActivityEvent.OpenNotificationSettings) },
                    transformation = SurfaceTransformation(transformationSpec),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .transformedHeight(this, transformationSpec)
                ) {
                    Text(
                        text = stringResource(R.string.grant_permission),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
