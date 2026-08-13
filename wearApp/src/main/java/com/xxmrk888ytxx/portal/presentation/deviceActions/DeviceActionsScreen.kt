package com.xxmrk888ytxx.portal.presentation.deviceActions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.model.Device
import com.xxmrk888ytxx.portal.domain.model.DeviceTransport

@Composable
fun DeviceActionsScreen(
    device: Device,
    onEvent: (DeviceActionsEvent) -> Unit
) {
    BackHandler { onEvent(DeviceActionsEvent.NavigateBack) }
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(onClick = { onEvent(DeviceActionsEvent.NavigateBack) }) {
                Text(stringResource(R.string.back))
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item { ListHeader { Text(device.name) } }
            item {
                Button(
                    onClick = {
                        onEvent(DeviceActionsEvent.Unlock(device.clientId))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.unlock))
                }
            }
            if (device.transport == DeviceTransport.WIFI && device.isWakeOnLanAvailable) {
                item {
                    Button(
                        onClick = {
                            onEvent(DeviceActionsEvent.WakeOnLanUnlock(device.clientId))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.wake_and_unlock))
                    }
                }
            }
        }
    }
}
