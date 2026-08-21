package com.xxmrk888ytxx.portal.presentation.deviceActions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.model.Device
import com.xxmrk888ytxx.portal.domain.model.DeviceTransport

/**
 * Shows actions for a selected synced device with rich UI, icons, and status feedback.
 *
 * The screen only emits [DeviceActionsEvent]. The phone performs the actual unlock operation.
 */
@Composable
fun DeviceActionsScreen(
    device: Device,
    isLoading: Boolean = false,
    onEvent: (DeviceActionsEvent) -> Unit
) {
    BackHandler { onEvent(DeviceActionsEvent.NavigateBack) }
    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(
                onClick = { onEvent(DeviceActionsEvent.NavigateBack) },
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.back))
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            TransformingLazyColumn(
                state = listState,
                contentPadding = contentPadding,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .transformedHeight(this, transformationSpec),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(
                                    when (device.transport) {
                                        DeviceTransport.WIFI -> R.drawable.ic_wifi
                                        DeviceTransport.BLUETOOTH -> R.drawable.ic_bluetooth
                                    }
                                ),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = when (device.transport) {
                                    DeviceTransport.WIFI -> stringResource(R.string.wifi)
                                    DeviceTransport.BLUETOOTH -> stringResource(R.string.bluetooth)
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            onEvent(DeviceActionsEvent.Unlock(device.clientId))
                        },
                        enabled = !isLoading,
                        transformation = SurfaceTransformation(transformationSpec),
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec)
                            .minimumVerticalContentPadding(
                                ButtonDefaults.minimumVerticalListContentPadding
                            ),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_lock_open),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(stringResource(R.string.unlock))
                        }
                    )
                }

                if (device.transport == DeviceTransport.WIFI && device.isWakeOnLanAvailable) {
                    item {
                        Button(
                            onClick = {
                                onEvent(DeviceActionsEvent.WakeOnLanUnlock(device.clientId))
                            },
                            enabled = !isLoading,
                            transformation = SurfaceTransformation(transformationSpec),
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec)
                                .minimumVerticalContentPadding(
                                    ButtonDefaults.minimumVerticalListContentPadding
                                ),
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_bolt),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(stringResource(R.string.wake_and_unlock))
                            }
                        )
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
