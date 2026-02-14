package com.xxmrk888ytxx.addnewdevicescreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenSideEffect
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenUiEvent
import com.xxmrk888ytxx.addnewdevicescreen.model.Page
import com.xxmrk888ytxx.addnewdevicescreen.model.ScreenState
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import kotlinx.coroutines.flow.Flow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.corecompose.LocalNavigator
import com.xxmrk888ytxx.corecompose.sharedUi.CenterAlignedTopAppBarWithBackArrow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewDeviceScreen(
    state: ScreenState,
    onEvent: (AddNewDeviceScreenUiEvent) -> Unit,
    sideEffect: Flow<AddNewDeviceScreenSideEffect>
) {
    val pager = rememberPagerState(0) { Page.entries.size }
    val navigator = LocalNavigator.current
    HandleSideEffect(sideEffect) { effect ->
        when (effect) {
            AddNewDeviceScreenSideEffect.NavigationBack -> navigator.navigateUp()
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
                        text = when(pageType) {
                            Page.SELECT_TYPE -> stringResource(R.string.protocol_selection)
                            Page.CONFIGURATION_WIFI -> "TODO()"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier,
                        textAlign = TextAlign.Center
                    )
                },
                onNavigateBack = { onEvent(AddNewDeviceScreenUiEvent.PreviousPage(pageType)) },
                actions = { },
            )
        },
        bottomBar = {
            Button(
                onClick = { onEvent(AddNewDeviceScreenUiEvent.NextPage(pageType)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = when(pageType) {
                    Page.SELECT_TYPE -> state !is ScreenState.NoSelectedType
                    Page.CONFIGURATION_WIFI -> false
                }
            ) {
                Text("Next")
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pager,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { pageId ->
            when (Page.fromInt(pageId)) {
                Page.SELECT_TYPE -> SelectTypePage(state, onEvent)
                Page.CONFIGURATION_WIFI -> {}
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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