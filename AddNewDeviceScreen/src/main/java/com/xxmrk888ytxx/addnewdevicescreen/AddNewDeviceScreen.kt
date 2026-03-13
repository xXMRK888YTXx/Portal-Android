package com.xxmrk888ytxx.addnewdevicescreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    val pager = rememberPagerState(0) { Page.entries.size }
    HandleSideEffect<AddNewDeviceScreenSideEffect>(sideEffect) { effect ->
        when (effect) {
            AddNewDeviceScreenSideEffect.ToBluetoothConfigurationPage -> TODO()
            AddNewDeviceScreenSideEffect.ToWifiConfigurationPage -> pager.animateScrollToPage(Page.CONFIGURATION_WIFI.id)
            is AddNewDeviceScreenSideEffect.ScrollToPage -> pager.animateScrollToPage(effect.pageId)
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
            onEvent(AddNewDeviceScreenUiEvent.SelectedWifi)
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