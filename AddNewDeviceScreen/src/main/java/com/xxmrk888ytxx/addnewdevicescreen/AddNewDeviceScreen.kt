package com.xxmrk888ytxx.addnewdevicescreen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenSideEffect
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenUiEvent
import com.xxmrk888ytxx.addnewdevicescreen.model.ScreenState
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import kotlinx.coroutines.flow.Flow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.xxmrk888ytxx.addnewdevicescreen.model.BluetoothDevice
import com.xxmrk888ytxx.addnewdevicescreen.model.BluetoothState
import com.xxmrk888ytxx.addnewdevicescreen.model.Page
import com.xxmrk888ytxx.addnewdevicescreen.model.Validator
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.sharedUi.CenterAlignedTopAppBarWithBackArrow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewDeviceScreen(
    state: ScreenState,
    onEvent: (AddNewDeviceScreenUiEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    val context = LocalContext.current
    val pager = rememberPagerState(0) { Page.entries.size }
    val requestBluetoothPermissionContract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onEvent(AddNewDeviceScreenUiEvent.UpdateBluetoothState)
    }
    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        onEvent(AddNewDeviceScreenUiEvent.UpdateBluetoothState)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onEvent(AddNewDeviceScreenUiEvent.UpdateBluetoothState)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    HandleSideEffect<AddNewDeviceScreenSideEffect>(sideEffect) { effect ->
        when (effect) {
            AddNewDeviceScreenSideEffect.ToBluetoothConfigurationPage -> pager.animateScrollToPage(
                Page.CONFIGURATION_BLUETOOTH.id
            )

            AddNewDeviceScreenSideEffect.ToWifiConfigurationPage -> pager.animateScrollToPage(Page.CONFIGURATION_WIFI.id)
            is AddNewDeviceScreenSideEffect.ScrollToPage -> pager.animateScrollToPage(effect.pageId)
            AddNewDeviceScreenSideEffect.RequestBluetoothPermission -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestBluetoothPermissionContract.launch(
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            }

            AddNewDeviceScreenSideEffect.EnableBluetooth -> enableBluetoothLauncher.launch(
                Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE
                )
            )

            AddNewDeviceScreenSideEffect.OpenBluetoothSettings -> {
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                context.startActivity(intent)
            }
        }
    }
    val pageType = remember(pager.currentPage) { Page.fromInt(pager.currentPage) }
    BackHandler(
        enabled = true,
    ) { onEvent(AddNewDeviceScreenUiEvent.PreviousPage(pageType)) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(),
        topBar = {
            CenterAlignedTopAppBarWithBackArrow(
                title = {
                    Text(
                        text = when (pageType) {
                            Page.SELECT_TYPE -> stringResource(R.string.protocol_selection)
                            Page.CONFIGURATION_WIFI -> stringResource(R.string.configuring_a_wi_fi_connection)
                            Page.CONFIGURATION_BLUETOOTH -> "Configuring a Bluetooth connection"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.basicMarquee(),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                },
                onNavigateBack = { onEvent(AddNewDeviceScreenUiEvent.PreviousPage(pageType)) },
                actions = { },
            )
        },
        bottomBar = {
            AnimatedContent(
                targetState = state.isLoading
            ) { isLoading ->
                if (!isLoading) {
                    Button(
                        onClick = {
                            val event = when (pageType) {
                                Page.CONFIGURATION_WIFI -> AddNewDeviceScreenUiEvent.ConnectToDevice
                                else -> AddNewDeviceScreenUiEvent.NextPage(pageType)
                            }
                            onEvent(event)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        enabled = when (pageType) {
                            Page.SELECT_TYPE -> state !is ScreenState.NoSelectedType
                            Page.CONFIGURATION_WIFI -> state is ScreenState.Wifi && state.isDataValid
                            Page.CONFIGURATION_BLUETOOTH -> false
                        }
                    ) {
                        Text(
                            text = when (pageType) {
                                else -> stringResource(R.string.next)
                            }
                        )
                    }
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pager,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) { pageId ->
            when (Page.fromInt(pageId)) {
                Page.SELECT_TYPE -> SelectTypePage(state, onEvent)
                Page.CONFIGURATION_WIFI -> WifiConfigurationPage(state, onEvent)
                Page.CONFIGURATION_BLUETOOTH -> BluetoothConfigurationPage(state, onEvent)
            }
        }
    }
}

@Composable
fun BluetoothConfigurationPage(
    screenState: ScreenState,
    onEvent: (AddNewDeviceScreenUiEvent) -> Unit
) {
    val state = remember(screenState) {
        screenState as? ScreenState.Bluetooth ?: ScreenState.Bluetooth()
    }

    val codeFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.bluetooth),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.configuring_a_bluetooth_connection),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.enter_the_6_digit_code_and_select_a_paired_device),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            OutlinedTextField(
                value = state.pairCode,
                onValueChange = {
                    onEvent(AddNewDeviceScreenUiEvent.PairCodeTextUpdated(it))
                },
                label = { Text(stringResource(R.string._6_digit_code)) },
                placeholder = { Text(stringResource(R.string._000000)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.password),
                        contentDescription = null
                    )
                },
                singleLine = true,
                isError = state.pairCode.isNotEmpty() && !Validator.isPairCodeValid(state.pairCode),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .focusRequester(codeFocusRequester)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onEvent(AddNewDeviceScreenUiEvent.OnScanQrCodeClicked)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.qr_code_scanner),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.scan_qr_code),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        when (val pairedState = state.pairedDevices) {
            is BluetoothState.Disabled -> {
                ErrorStateView(
                    painter = painterResource(R.drawable.bluetooth_disabled),
                    message = stringResource(R.string.bluetooth_is_disabled_please_enable_it_to_scan_for_devices),
                    iconTint = MaterialTheme.colorScheme.error,
                    buttonText = stringResource(R.string.enable),
                    onButtonClick = { onEvent(AddNewDeviceScreenUiEvent.EnableBluetooth) }
                )
            }

            is BluetoothState.PermissionDenied -> {
                ErrorStateView(
                    painter = painterResource(R.drawable.bluetooth_disabled),
                    message = stringResource(R.string.permission_denied_please_grant_bluetooth_access_in_app_settings),
                    iconTint = MaterialTheme.colorScheme.error,
                    buttonText = stringResource(R.string.grant),
                    onButtonClick = { onEvent(AddNewDeviceScreenUiEvent.RequestBluetoothPermission) }
                )
            }

            is BluetoothState.NotSupported -> {
                ErrorStateView(
                    painter = painterResource(R.drawable.block),
                    message = stringResource(R.string.bluetooth_is_not_supported_on_this_device),
                    iconTint = MaterialTheme.colorScheme.error
                )
            }

            is BluetoothState.Success -> {
                if (pairedState.pairedDevices.isEmpty()) {
                    ErrorStateView(
                        painter = painterResource(R.drawable.bluetooth_searching),
                        message = stringResource(R.string.no_paired_devices_found_please_pair_a_device_in_system_settings),
                        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                        onButtonClick = { onEvent(AddNewDeviceScreenUiEvent.OpenBluetoothSettings) }
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = stringResource(R.string.paired_devices),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        DeviceList(
                            devices = pairedState.pairedDevices,
                            selectedDevice = state.selectedDevice,
                            onEvent = onEvent
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        onEvent(AddNewDeviceScreenUiEvent.OpenBluetoothSettings)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = if (pairedState.pairedDevices.isNotEmpty()) stringResource(R.string.can_t_find_your_pc_pair_in_settings)
                        else stringResource(R.string.pair_a_device_in_settings)
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceList(
    devices: List<BluetoothDevice>,
    selectedDevice: BluetoothDevice?,
    onEvent: (AddNewDeviceScreenUiEvent) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        devices.forEach { device ->
            val isSelected = remember(device, selectedDevice) {
                device.macAddress == selectedDevice?.macAddress
            }

            ListItem(
                headlineContent = {
                    val isBlankName = remember(device.name) {
                        device.name.isBlank()
                    }
                    Text(
                        text = if (!isBlankName) device.name else stringResource(R.string.unknown_device),
                        fontStyle = if (isBlankName) FontStyle.Italic else FontStyle.Normal
                    )
                },
                supportingContent = {
                    Text(text = device.macAddress)
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.bluetooth),
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    if (isSelected) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        onEvent(AddNewDeviceScreenUiEvent.OnBluetoothDeviceSelected(device))
                    }
            )
        }
    }
}

@Composable
private fun ErrorStateView(
    painter: Painter,
    message: String,
    iconTint: androidx.compose.ui.graphics.Color,
    buttonText: String? = null,          // Optional
    onButtonClick: (() -> Unit)? = null  // Optional
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = iconTint
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Если передали и текст, и действие — показываем кнопку
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton( // Используем OutlinedButton, чтобы не перетягивать всё внимание
                onClick = onButtonClick
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun WifiConfigurationPage(state: ScreenState, onEvent: (AddNewDeviceScreenUiEvent) -> Unit) {
    val ipFocusRequester = remember { FocusRequester() }
    val codeFocusRequester = remember { FocusRequester() }
    val state = remember(state) {
        state as? ScreenState.Wifi ?: ScreenState.Wifi()
    }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Icon(
                painter = painterResource(R.drawable.wifi),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.configuring_a_wi_fi_connection),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.enter_the_details_specified_on_the_pc),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Device Name Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = state.deviceName,
                    onValueChange = {
                        onEvent(AddNewDeviceScreenUiEvent.DeviceNameTextUpdated(it))
                    },
                    label = { Text(stringResource(R.string.device_name)) },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.for_example_my_pc_name_pcname_username),
                            modifier = Modifier.basicMarquee(),
                            maxLines = 1
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.computer),
                            contentDescription = null
                        )
                    },
                    isError = state.deviceName.isNotEmpty() && !Validator.isDeviceNameValid(state.deviceName),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { ipFocusRequester.requestFocus() }
                    ), modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // IP Address Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = state.host,
                    onValueChange = {
                        onEvent(AddNewDeviceScreenUiEvent.HostTextUpdated(it))
                    },
                    label = { Text(stringResource(R.string.ip_address)) },
                    placeholder = { Text(stringResource(R.string._192_168_x_x)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.wifi),
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    isError = state.host.isNotEmpty() && !Validator.isHostValid(state.host),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { codeFocusRequester.requestFocus() }
                    ), modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .focusRequester(ipFocusRequester)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Code Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = state.pairCode,
                    onValueChange = {
                        onEvent(AddNewDeviceScreenUiEvent.PairCodeTextUpdated(it))
                    },
                    label = { Text(stringResource(R.string._6_digit_code)) },
                    placeholder = { Text(stringResource(R.string._000000)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.password),
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    isError = state.pairCode.isNotEmpty() && !Validator.isPairCodeValid(state.pairCode),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ), modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .focusRequester(codeFocusRequester)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    onEvent(AddNewDeviceScreenUiEvent.OnScanQrCodeClicked)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.qr_code_scanner),
                    contentDescription = "Scan QR Code",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.scan_qr_code),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}


@Composable
fun SelectTypePage(state: ScreenState, onEvent: (AddNewDeviceScreenUiEvent) -> Unit) {

    val selectWifiAction = remember {
        {
            onEvent(AddNewDeviceScreenUiEvent.SelectedWifi)
        }
    }

    val selectBluetoothAction = remember {
        {
            onEvent(AddNewDeviceScreenUiEvent.SelectedBluetooth)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        // WiFi Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = selectWifiAction
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = state is ScreenState.Wifi,
                    onClick = selectWifiAction
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.wifi),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.wifi_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Bluetooth Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = selectBluetoothAction,

            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = state is ScreenState.Bluetooth,
                    onClick = selectBluetoothAction
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.bluetooth),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.bluetooth_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

}