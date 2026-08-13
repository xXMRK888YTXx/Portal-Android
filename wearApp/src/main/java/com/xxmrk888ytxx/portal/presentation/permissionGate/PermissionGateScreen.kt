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
import androidx.wear.compose.material3.Text
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.presentation.mainActivity.MainActivityEvent
import com.xxmrk888ytxx.portal.presentation.mainActivity.MainScreenState

@Composable
fun PermissionGateScreen(
    state: MainScreenState,
    onEvent: (MainActivityEvent) -> Unit
) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item { ListHeader { Text(stringResource(R.string.permissions_title)) } }
            item {
                Text(
                    text = stringResource(R.string.notification_permission_required),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Button(
                    onClick = { onEvent(MainActivityEvent.OpenNotificationSettings) },
                    modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
