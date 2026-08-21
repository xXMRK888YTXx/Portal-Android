package com.xxmrk888ytxx.portal.presentation.permissionGate

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.presentation.mainActivity.MainActivityEvent
import com.xxmrk888ytxx.portal.presentation.mainActivity.MainScreenState

/**
 * Blocks access to the main Wear OS UI until notification permission is granted.
 *
 * Notifications are required because incoming requests on inactive watches are delivered through
 * notification-only UX.
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
            contentPadding = contentPadding
        ) {
            item {
                ListHeader(
                    transformation = SurfaceTransformation(transformationSpec),
                    modifier = Modifier.transformedHeight(this, transformationSpec)
                ) {
                    Text(stringResource(R.string.permissions_title))
                }
            }
            item {
                Text(
                    text = stringResource(R.string.notification_permission_required),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                )
            }
            item {
                Button(
                    onClick = { onEvent(MainActivityEvent.OpenNotificationSettings) },
                    transformation = SurfaceTransformation(transformationSpec),
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                ) {
                    Text(stringResource(R.string.notifications))
                }
            }
            item {
                Text(
                    text = stringResource(
                        R.string.permission_status_notifications,
                        if (state.permissions.canPostNotifications) {
                            stringResource(R.string.enabled)
                        } else {
                            stringResource(R.string.disabled)
                        }
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                )
            }
        }
    }
}
