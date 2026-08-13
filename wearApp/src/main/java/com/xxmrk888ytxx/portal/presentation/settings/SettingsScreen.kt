package com.xxmrk888ytxx.portal.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import com.xxmrk888ytxx.portal.BuildConfig
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.presentation.mainActivity.MainScreenState

@Composable
fun SettingsScreen(
    state: MainScreenState,
    isPhoneConnected: Boolean?,
    onEvent: (SettingsEvent) -> Unit
) {
    BackHandler { onEvent(SettingsEvent.NavigateBack) }
    val listState = rememberTransformingLazyColumnState()

    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(onClick = { onEvent(SettingsEvent.NavigateBack) }) {
                Text(stringResource(R.string.back))
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item { ListHeader { Text(stringResource(R.string.settings)) } }
            item {
                TextButton(
                    onClick = { onEvent(SettingsEvent.OpenNotificationSettings) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(
                            R.string.permission_status_notifications,
                            if (state.permissions.canPostNotifications) {
                                stringResource(R.string.enabled)
                            } else {
                                stringResource(R.string.disabled)
                            }
                        )
                    )
                }
            }
            item {
                TextButton(
                    onClick = { onEvent(SettingsEvent.RefreshPhoneConnection) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(
                            R.string.phone_connection_status,
                            when (isPhoneConnected) {
                                true -> stringResource(R.string.connected)
                                false -> stringResource(R.string.disconnected)
                                null -> stringResource(R.string.checking)
                            }
                        )
                    )
                }
            }
            item {
                Text(
                    text = stringResource(R.string.version_name, BuildConfig.VERSION_NAME),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
