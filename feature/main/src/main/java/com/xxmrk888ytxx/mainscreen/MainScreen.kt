package com.xxmrk888ytxx.mainscreen

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.corecompose.MacAddressTransformation
import com.xxmrk888ytxx.corecompose.uiText.asString
import com.xxmrk888ytxx.mainscreen.model.Device
import com.xxmrk888ytxx.mainscreen.model.DeviceType
import com.xxmrk888ytxx.mainscreen.model.DevicesRemovedBannerState
import com.xxmrk888ytxx.mainscreen.model.DialogState
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.ActivityInOnResumeState
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.AddNewDevice
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.CreateShortcut
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.DismissDevicesRemovedBanner
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.DismissDialog
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.OnIsRequiredBiometricUnlockStateChanged
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.OnIsTryToSendEnabledChanged
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.OnMacAddressChanged
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.PermissionGranted
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.SaveWOLMacAddress
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.SendUnlockRequest
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.SendWOLRequest
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.ShowCreateShortcutModelDialog
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.ToDeviceDetailsScreen
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent.WakeUpOnLANClicked
import com.xxmrk888ytxx.mainscreen.model.MainScreenSideEffect
import com.xxmrk888ytxx.mainscreen.model.Permission
import com.xxmrk888ytxx.mainscreen.model.PermissionBannerItem
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import kotlinx.coroutines.flow.Flow
import com.xxmrk888ytxx.corecompose.R as ComposeR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    screenState: ScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {

    val grantedPermissionHandler: (Permission) -> Unit = remember {
        {
            onEvent(PermissionGranted(it))
        }
    }

    val requestNotificationPermissionContract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) grantedPermissionHandler(Permission.Notification)
    }

    val requestNearbyDevicesPermissionContract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) grantedPermissionHandler(Permission.NearbyDevices)
    }

    HandleSideEffect<MainScreenSideEffect>(sideEffect) {
        when (it) {
            MainScreenSideEffect.RequestNearbyDevicesPermission -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNearbyDevicesPermissionContract.launch(
                    Manifest.permission.BLUETOOTH_CONNECT)
            }

            MainScreenSideEffect.RequestNotificationPermission -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermissionContract.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }
    Scaffold(
        Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onEvent(AddNewDevice) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_add),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.add_pc),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets(),
        topBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.devices),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.tap_card_to_unlock),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AnimatedVisibility(screenState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedVisibility(visible = screenState.permissionBannerItemList.isNotEmpty()) {
                PermissionBannersPager(
                    banners = screenState.permissionBannerItemList,
                    onEvent = onEvent,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            AnimatedVisibility(
                visible = screenState.devicesRemovedBannerState !is DevicesRemovedBannerState.None
            ) {
                DevicesRemovedBanner(screenState.devicesRemovedBannerState) {
                    onEvent(DismissDevicesRemovedBanner)
                }
            }

            Box(Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = screenState.devices,
                    label = "DevicesContent"
                ) { devices ->
                    when (devices.isNotEmpty()) {
                        true -> DeviceList(screenState, onEvent)
                        false -> EmptyDevicesState(onEvent)
                    }
                }
            }
        }

        when(screenState.dialogState) {
            DialogState.Hidden -> {}
            is DialogState.ShortcutDialog -> CreateShortcutBottomSheet(
                onDismiss = {
                    onEvent(DismissDialog)
                },
                onCreateClick = {
                    onEvent(CreateShortcut)
                },
                onIsRequiredBiometricUnlockStateChanged = {
                    onEvent(OnIsRequiredBiometricUnlockStateChanged(it))
                },
                dialogState = screenState.dialogState,
                onSendWolRequestChanged = {
                    onEvent(MainScreenEvent.OnIsRequiredSendWOLRequestChanged(it))
                },
            )

            is DialogState.EnterMacAddressDialog -> EnterMacAddressDialog(
                dialogState = screenState.dialogState,
                onDismiss = { onEvent(DismissDialog) },
                onMacAddressChanged = { onEvent(OnMacAddressChanged(it)) },
                onConfirmClick = { onEvent(SaveWOLMacAddress) }
            )

            is DialogState.WALRequestDialog -> WALRequestDialog(
                dialogState = screenState.dialogState,
                onDismiss = { onEvent(DismissDialog) },
                onIsTryToSendEnabledChanged = { onEvent(OnIsTryToSendEnabledChanged(it)) },
                onSendClick = { onEvent(SendWOLRequest) }
            )
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onEvent(ActivityInOnResumeState)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WALRequestDialog(
    dialogState: DialogState.WALRequestDialog,
    onDismiss: () -> Unit,
    onIsTryToSendEnabledChanged: (Boolean) -> Unit,
    onSendClick: () -> Unit
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
                text = stringResource(R.string.send_wake_up_on_lan_request),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .toggleable(
                        value = dialogState.isTryToSendUnlockRequestEnabled,
                        onValueChange = onIsTryToSendEnabledChanged,
                        role = Role.Switch
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.send_unlock_requests),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.the_application_will_attempt_to_unlock_your_pc_within_3_minutes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = dialogState.isTryToSendUnlockRequestEnabled,
                    onCheckedChange = null
                )
            }

            Button(
                onClick = onSendClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.send))
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterMacAddressDialog(
    dialogState: DialogState.EnterMacAddressDialog,
    onDismiss: () -> Unit,
    onMacAddressChanged: (String) -> Unit,
    onConfirmClick: () -> Unit
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
                text = stringResource(R.string.enter_mac_address),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = dialogState.enteredMac,
                onValueChange = onMacAddressChanged,
                label = { Text(stringResource(R.string.mac_address)) },
                placeholder = { Text("00:00:00:00:00:2B") },
                singleLine = true,
                visualTransformation = MacAddressTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )

            Button(
                onClick = onConfirmClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = dialogState.isValidateMacAddress,
            ) {
                Text(stringResource(R.string.confirm))
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}

@Composable
fun DevicesRemovedBanner(
    devicesRemovedBannerState: DevicesRemovedBannerState,
    modifier: Modifier = Modifier,
    onOkClick: () -> Unit
) {
    if (devicesRemovedBannerState is DevicesRemovedBannerState.None) return
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.priority),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.all_devices_removed),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when(devicesRemovedBannerState) {
                    DevicesRemovedBannerState.None -> ""
                    DevicesRemovedBannerState.RemovedByChangesInBiometricEnvironment -> stringResource(
                        R.string.changes_have_been_detected_in_the_biometric_data_all_paired_devices_have_been_removed
                    )
                    DevicesRemovedBannerState.RemovedBySecurityChanges -> stringResource(R.string.we_have_changed_the_security_settings_all_devices_have_been_removed)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onOkClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.ok))
            }
        }
    }
}


@Composable
fun DeviceList(
    screenState: ScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Фильтруем устройства по типу
    val wifiDevices = remember(screenState.devices) {
        screenState.devices.filter { it.deviceType == DeviceType.WIFI }
    }
    val bluetoothDevices = remember(screenState.devices) {
        screenState.devices.filter { it.deviceType == DeviceType.BLUETOOTH }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (wifiDevices.isNotEmpty()) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.wifi),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${stringResource(R.string.wi_fi).uppercase()} (${wifiDevices.size})",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            items(
                items = wifiDevices,
                key = { it.clientId }
            ) { device ->
                DeviceItem(
                    device = device,
                    isUnlockButtonAvailable = !screenState.isLoading,
                    onEvent = onEvent
                )
            }
        }

        if (bluetoothDevices.isNotEmpty()) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(
                        start = 4.dp,
                        bottom = 4.dp,
                        top = if (wifiDevices.isNotEmpty()) 12.dp else 0.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.bluetooth),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${stringResource(R.string.bluetooth).uppercase()} (${bluetoothDevices.size})",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            items(
                items = bluetoothDevices,
                key = { it.clientId }
            ) { device ->
                DeviceItem(
                    device = device,
                    isUnlockButtonAvailable = !screenState.isLoading,
                    onEvent = onEvent
                )
            }
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
    val hasError = device.isHaveErrorsWithDevice
    val errorColor = MaterialTheme.colorScheme.error
    val cardBorder = if (hasError) {
        BorderStroke(1.5.dp, errorColor)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }

    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        onClick = {
            if (!hasError) {
                onEvent(SendUnlockRequest(device))
            } else {
                onEvent(ToDeviceDetailsScreen(device.clientId))
            }
        },
        modifier = modifier.fillMaxWidth(),
        enabled = isUnlockButtonAvailable,
        shape = RoundedCornerShape(26.dp),
        border = cardBorder,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Squircle icon container
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (hasError) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        when (device.deviceType) {
                            DeviceType.WIFI -> R.drawable.wifi
                            DeviceType.BLUETOOTH -> R.drawable.bluetooth
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = if (hasError) MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Info column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = device.deviceName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = device.host,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (hasError) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.error),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = errorColor
                        )
                        Text(
                            text = stringResource(R.string.an_error_occurred_go_to_options_for_details),
                            style = MaterialTheme.typography.labelSmall,
                            color = errorColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.lock_open),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.tap_to_unlock),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Action buttons: Options (settings) and Overflow (three dots)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = { onEvent(ToDeviceDetailsScreen(device.clientId)) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.options),
                        contentDescription = stringResource(R.string.options),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(ComposeR.drawable.more_vert),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (device.deviceType == DeviceType.WIFI) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.send_wake_up_on_lan_request)) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.lan),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onEvent(WakeUpOnLANClicked(device))
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.create_shortcut)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.shortcut),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onEvent(ShowCreateShortcutModelDialog(device))
                            }
                        )
                    }
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

        Button(onClick = { onEvent(AddNewDevice) }) {
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
    dialogState: DialogState.ShortcutDialog,
    onDismiss: () -> Unit,
    onIsRequiredBiometricUnlockStateChanged: (Boolean) -> Unit,
    onSendWolRequestChanged: (Boolean) -> Unit,
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
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.create_shortcut),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            SettingsRowWithDescription(
                title = stringResource(R.string.use_biometric_authentication),
                description = stringResource(R.string.to_send_a_request_you_will_need_to_pass_a_biometrics_check),
                errorMessage =
                    when {
                        !dialogState.isBiometricUnlockAvailable -> stringResource(R.string.biometrics_is_not_enabled_in_the_app_settings)
                        dialogState.isUnsafeUnlockTypesDisabled -> stringResource(R.string.this_parameter_cannot_be_controlled_the_inhibit_insecure_unlock_methods_setting_is_enabled)
                        else -> null
                    },
                checked = dialogState.isRequiredBiometricUnlock,
                enabled = dialogState.isBiometricUnlockAvailable && !dialogState.isUnsafeUnlockTypesDisabled,
                onCheckedChange = onIsRequiredBiometricUnlockStateChanged
            )

            if (dialogState.isWOLVisible) {
                SettingsRowWithDescription(
                    title = stringResource(R.string.send_wake_up_on_lan_request),
                    description = stringResource(R.string.the_application_will_attempt_to_unlock_your_pc_within_3_minutes),
                    errorMessage = if (!dialogState.isWOLAvailable) {
                        stringResource(R.string.the_device_s_mac_address_is_not_specified)
                    } else null,
                    checked = dialogState.isWolEnabled,
                    enabled = dialogState.isWOLAvailable,
                    onCheckedChange = onSendWolRequestChanged
                )
            }

            Button(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(stringResource(R.string.create))
            }
        }
    }
}

@Composable
private fun SettingsRowWithDescription(
    title: String,
    description: String,
    errorMessage: String?,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange
            )
            .alpha(if (enabled) 1f else 0.6f)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (errorMessage == null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled
        )
    }
}

@Composable
fun PermissionBannersPager(
    banners: List<PermissionBannerItem>,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            key = { banners[it].hashCode() }
        ) { page ->
            PermissionBannerCard(
                banner = banners[page],
                onEvent = onEvent
            )
        }

        if (banners.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(banners.size) { iteration ->
                    val color by animateColorAsState(
                        targetValue = if (pagerState.currentPage == iteration) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        },
                        animationSpec = tween(durationMillis = 300),
                        label = "indicator_color"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionBannerCard(
    banner: PermissionBannerItem,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showExpandButton by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    min = 160.dp,
                    max = if (isExpanded) Dp.Infinity else 160.dp
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = banner.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = banner.title.asString(),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = banner.description.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { textLayoutResult ->
                            if (!isExpanded) {
                                showExpandButton = textLayoutResult.hasVisualOverflow
                            }
                        }
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showExpandButton) {
                    TextButton(
                        onClick = { isExpanded = !isExpanded },
                        contentPadding = PaddingValues(horizontal = 0.dp)
                    ) {
                        Text(
                            text = if (isExpanded) stringResource(R.string.show_less) else stringResource(R.string.show_more),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onEvent(banner.eventForRequestPermission) }
                ) {
                    Text(stringResource(R.string.grant))
                }
            }
        }
    }
}