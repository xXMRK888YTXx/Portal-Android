package com.xxmrk888ytxx.mainscreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.mainscreen.model.CreateShortcutDialogState
import com.xxmrk888ytxx.mainscreen.model.Device
import com.xxmrk888ytxx.mainscreen.model.DeviceAction
import com.xxmrk888ytxx.mainscreen.model.DeviceType
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.MainScreenSideEffect
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    screenState: ScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {

    val sheetState = rememberModalBottomSheetState()
    val isCreateShortcutDialogVisible = remember(screenState.createShortcutDialogState) {
        screenState.createShortcutDialogState is CreateShortcutDialogState.Showed
    }


    HandleSideEffect<MainScreenSideEffect>(sideEffect) {}
    Scaffold(
        Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(MainScreenEvent.AddNewDevice) }
            ) {
                Icon(painter = painterResource(R.drawable.outline_add), contentDescription = null)
            }
        },
        contentWindowInsets = WindowInsets(),
        topBar = {
            Column(
                Modifier.fillMaxWidth()
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.devices),
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.basicMarquee(),
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    },
                    windowInsets = WindowInsets()
                )

                AnimatedVisibility(screenState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = screenState.devices
            ) { devices ->
                when (devices.isNotEmpty()) {
                    true -> DeviceList(screenState, onEvent)
                    false -> EmptyDevicesState(onEvent)
                }
            }
        }

        if (screenState.createShortcutDialogState is CreateShortcutDialogState.Showed) {
            CreateShortcutBottomSheet(
                onDismiss = {
                    onEvent(MainScreenEvent.DismissCreateShortcutModelDialog)
                },
                onCreateClick = {
                    onEvent(MainScreenEvent.CreateShortcut)
                },
                onIsRequiredBiometricUnlockStateChanged = {
                    onEvent(MainScreenEvent.OnIsRequiredBiometricUnlockStateChanged(it))
                },
                createShortcutDialogState = screenState.createShortcutDialogState,
            )
        }
    }
}


@Composable
fun DeviceList(
    screenState: ScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = screenState.devices,
            key = { it.deviceId }
        ) { device ->
            DeviceItem(
                device = device,
                isUnlockButtonAvailable = !screenState.isLoading,
                onEvent = onEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceItem(
    device: Device,
    isUnlockButtonAvailable: Boolean,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // Define actions in one place — easy to extend
    val actions = remember(isUnlockButtonAvailable) {
        listOf(
            DeviceAction(
                label = "Unlock",
                icon = R.drawable.lock_open,
                enabled = isUnlockButtonAvailable,
                onClick = { onEvent(MainScreenEvent.SendUnlockRequest(device)) }
            ),
            DeviceAction(
                label = "Create Shortcut",
                icon = R.drawable.shortcut,
                enabled = true,
                onClick = { onEvent(MainScreenEvent.ShowCreateShortcutModelDialog(device)) }
            ),
            // Add more actions here as needed
        )
    }

    Card(
        onClick = {
            onEvent(MainScreenEvent.ToDeviceDetailsScreen(device.deviceId))
        },
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            // — Device info row —
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(
                        when (device.deviceType) {
                            DeviceType.WIFI -> R.drawable.wifi
                            DeviceType.BLUETOOTH -> R.drawable.bluetooth
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = device.deviceName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = device.host,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // — Action chips row —
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(actions) { action ->
                    SuggestionChip(
                        onClick = action.onClick,
                        enabled = action.enabled,
                        label = {
                            Text(
                                text = action.label,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        icon = {
                            Icon(
                                painter = painterResource(action.icon),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.alpha(if (action.enabled) 1f else 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyDevicesState(onEvent: (MainScreenEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.computer),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.no_devices_added),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.add_a_new_device_to_get_started),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onEvent(MainScreenEvent.AddNewDevice) }) {
            Icon(
                painter = painterResource(R.drawable.outline_add),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(R.string.add_device))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateShortcutBottomSheet(
    createShortcutDialogState: CreateShortcutDialogState.Showed,
    onDismiss: () -> Unit,
    onIsRequiredBiometricUnlockStateChanged: (Boolean) -> Unit,
    onCreateClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.create_shortcut),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.use_biometric_authentication),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )

                Switch(
                    checked = createShortcutDialogState.isRequiredBiometricUnlock,
                    onCheckedChange = onIsRequiredBiometricUnlockStateChanged
                )
            }

            Button(
                onClick = onCreateClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.create))
            }
        }
    }
}