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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenSideEffect
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenUiEvent
import com.xxmrk888ytxx.addnewdevicescreen.model.BluetoothDevice
import com.xxmrk888ytxx.addnewdevicescreen.model.BluetoothState
import com.xxmrk888ytxx.addnewdevicescreen.model.Page
import com.xxmrk888ytxx.addnewdevicescreen.model.ScreenState
import com.xxmrk888ytxx.addnewdevicescreen.model.Validator
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.corecompose.sharedUi.CenterAlignedTopAppBarWithBackArrow
import kotlinx.coroutines.flow.Flow

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
                            Page.CONFIGURATION_BLUETOOTH -> stringResource(R.string.configuring_a_bluetooth_connection)
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.basicMarquee(),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                },
                onNavigateBack = { onEvent(AddNewDeviceScreenUiEvent.PreviousPage(pageType)) },
                actions = { }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    val isButtonEnabled = !state.isLoading && when (pageType) {
                        Page.SELECT_TYPE -> state !is ScreenState.NoSelectedType
                        Page.CONFIGURATION_WIFI -> state is ScreenState.Wifi && state.isDataValid
                        Page.CONFIGURATION_BLUETOOTH -> state is ScreenState.Bluetooth && state.isDataValid
                    }

                    Button(
                        onClick = {
                            val event = when (pageType) {
                                Page.CONFIGURATION_WIFI, Page.CONFIGURATION_BLUETOOTH -> AddNewDeviceScreenUiEvent.ConnectToDevice
                                else -> AddNewDeviceScreenUiEvent.NextPage(pageType)
                            }
                            onEvent(event)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        enabled = isButtonEnabled
                    ) {
                        AnimatedContent(targetState = state.isLoading, label = "button_content") { isLoading ->
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = when (pageType) {
                                        Page.CONFIGURATION_WIFI, Page.CONFIGURATION_BLUETOOTH -> stringResource(R.string.connect)
                                        else -> stringResource(R.string.next)
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                pageType == Page.CONFIGURATION_WIFI || pageType == Page.CONFIGURATION_BLUETOOTH
            ) {
                FloatingActionButton(
                    onClick = { onEvent(AddNewDeviceScreenUiEvent.OnScanQrCodeClicked) },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.qr_code_scanner),
                        contentDescription = stringResource(R.string.scan_qr_code),
                        modifier = Modifier.size(24.dp)
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
    val deviceNameFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Section Header
        Text(
            text = stringResource(R.string.connection_parameters),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        // Single Grouped Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    isError = state.deviceName.isNotEmpty() && !Validator.isDeviceNameValid(state.deviceName),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { codeFocusRequester.requestFocus() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(deviceNameFocusRequester)
                )

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
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
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
                        .focusRequester(codeFocusRequester)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Paired devices section
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
                Text(
                    text = stringResource(R.string.paired_devices).uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                if (pairedState.pairedDevices.isEmpty()) {
                    ErrorStateView(
                        painter = painterResource(R.drawable.bluetooth_searching),
                        message = stringResource(R.string.no_paired_devices_found_please_pair_a_device_in_system_settings),
                        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                        onButtonClick = { onEvent(AddNewDeviceScreenUiEvent.OpenBluetoothSettings) }
                    )
                } else {
                    DeviceList(
                        devices = pairedState.pairedDevices,
                        selectedDevice = state.selectedDevice,
                        onEvent = onEvent
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        onEvent(AddNewDeviceScreenUiEvent.OpenBluetoothSettings)
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
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
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        devices.forEach { device ->
            val isSelected = remember(device, selectedDevice) {
                device.macAddress == selectedDevice?.macAddress
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                onClick = { onEvent(AddNewDeviceScreenUiEvent.OnBluetoothDeviceSelected(device)) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.bluetooth),
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        val isBlankName = remember(device.name) { device.name.isBlank() }
                        Text(
                            text = if (!isBlankName) device.name else stringResource(R.string.unknown_device),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            fontStyle = if (isBlankName) FontStyle.Italic else FontStyle.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = device.macAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isSelected) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorStateView(
    painter: Painter,
    message: String,
    iconTint: androidx.compose.ui.graphics.Color,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
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
            modifier = Modifier.size(56.dp),
            tint = iconTint
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = onButtonClick,
                shape = RoundedCornerShape(16.dp)
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Quick QR Code Card (as in Stitch mockup)
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
            ),
            onClick = { onEvent(AddNewDeviceScreenUiEvent.OnScanQrCodeClicked) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.qr_code_scanner),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.fast_qr_scan_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.fast_qr_scan_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.arrow_forward),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section header for manual inputs
        Text(
            text = stringResource(R.string.connection_parameters),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        // Single Grouped Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Device name field
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
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    isError = state.deviceName.isNotEmpty() && !Validator.isDeviceNameValid(state.deviceName),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { ipFocusRequester.requestFocus() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Host field
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
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    isError = state.host.isNotEmpty() && !Validator.isHostValid(state.host),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { codeFocusRequester.requestFocus() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(ipFocusRequester)
                )

                // Pair code field
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
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
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
                        .focusRequester(codeFocusRequester)
                )
            }
        }
    }
}

@Composable
fun SelectTypePage(state: ScreenState, onEvent: (AddNewDeviceScreenUiEvent) -> Unit) {
    val selectWifiAction = remember { { onEvent(AddNewDeviceScreenUiEvent.SelectedWifi) } }
    val selectBluetoothAction = remember { { onEvent(AddNewDeviceScreenUiEvent.SelectedBluetooth) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.protocol_selection),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.enter_the_details_specified_on_the_pc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // WiFi Card
        val isWifiSelected = state is ScreenState.Wifi
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                width = if (isWifiSelected) 2.dp else 1.dp,
                color = if (isWifiSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (isWifiSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
            ),
            onClick = selectWifiAction
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isWifiSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.wifi),
                            contentDescription = null,
                            tint = if (isWifiSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.wifi),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = stringResource(R.string.recommended),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.wifi_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                RadioButton(
                    selected = isWifiSelected,
                    onClick = selectWifiAction
                )
            }
        }

        // Bluetooth Card
        val isBluetoothSelected = state is ScreenState.Bluetooth
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                width = if (isBluetoothSelected) 2.dp else 1.dp,
                color = if (isBluetoothSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (isBluetoothSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
            ),
            onClick = selectBluetoothAction
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isBluetoothSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.bluetooth),
                            contentDescription = null,
                            tint = if (isBluetoothSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.bluetooth),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.bluetooth_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                RadioButton(
                    selected = isBluetoothSelected,
                    onClick = selectBluetoothAction
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // PC Client Footnote Card
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.info),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.download_pc_client_footnote),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}