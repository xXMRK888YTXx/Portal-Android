package com.xxmrk888ytxx.mainscreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.corecompose.LocalNavigator
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
    val navigator = LocalNavigator.current
    HandleSideEffect<MainScreenSideEffect>(sideEffect) { effect ->
        when (effect) {
            MainScreenSideEffect.NavigateToAddNewDeviceScreen -> navigator.fromMainScreenToAddNewDeviceScreen()
        }
    }
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
                    true -> {}
                    false -> EmptyDevicesState(onEvent)
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