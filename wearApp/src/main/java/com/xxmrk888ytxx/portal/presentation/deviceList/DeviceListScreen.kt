package com.xxmrk888ytxx.portal.presentation.deviceList

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.model.Device
import com.xxmrk888ytxx.portal.domain.model.DeviceTransport

/**
 * Displays PC profiles synced from the phone.
 *
 * All user actions are emitted as [DeviceListEvent]; this Composable does not call ViewModel or
 * navigation methods directly.
 */
@Composable
fun DeviceListScreen(
    devices: List<Device>,
    onEvent: (DeviceListEvent) -> Unit
) {
    val listState = rememberTransformingLazyColumnState()
    var refreshDrag by remember { mutableFloatStateOf(0f) }
    val refreshThreshold = with(LocalDensity.current) { 56.dp.toPx() }

    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(onClick = { onEvent(DeviceListEvent.OpenSettings) }) {
                Text(stringResource(R.string.settings))
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            modifier = Modifier.pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (refreshDrag > refreshThreshold) {
                            onEvent(DeviceListEvent.RefreshDevices)
                        }
                        refreshDrag = 0f
                    },
                    onDragCancel = { refreshDrag = 0f }
                ) { _, dragAmount ->
                    if (dragAmount > 0) refreshDrag += dragAmount
                }
            }
        ) {
            item { ListHeader { Text(stringResource(R.string.devices)) } }
            item {
                TextButton(
                    onClick = { onEvent(DeviceListEvent.RefreshDevices) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.refresh_devices))
                }
            }
            if (devices.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_synced_devices),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(devices) { device ->
                    Card(
                        onClick = { onEvent(DeviceListEvent.SelectDevice(device)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(device.name)
                            Text(
                                text = when (device.transport) {
                                    DeviceTransport.WIFI -> stringResource(R.string.wifi)
                                    DeviceTransport.BLUETOOTH -> stringResource(R.string.bluetooth)
                                },
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
