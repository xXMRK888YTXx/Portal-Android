package com.xxmrk888ytxx.deviceconfigurationscreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.corecompose.sharedUi.CenterAlignedTopAppBarWithBackArrow
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationUiEvent
import com.xxmrk888ytxx.deviceconfigurationscreen.model.ScreenState
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceConfigurationScreen(
    screenState: ScreenState,
    onEvent: (DeviceConfigurationUiEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    HandleSideEffect<SideEffect>(sideEffect) {}


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(),
        topBar = {
            if (screenState is ScreenState.DeviceInfo) {
                CenterAlignedTopAppBarWithBackArrow(
                    title = {
                        Text(
                            text = screenState.device.deviceName,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.basicMarquee(),
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                ) {
                    onEvent(DeviceConfigurationUiEvent.NavigateBack)
                }
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            screenState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { screenState ->
            when (screenState) {
                is ScreenState.DeviceInfo -> DeviceInfoState(screenState, onEvent)
                ScreenState.Loading -> LoadingState()
            }
        }
    }
}

@Composable
fun DeviceInfoState(
    screenState: ScreenState.DeviceInfo,
    onEvent: (DeviceConfigurationUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoItem(title = stringResource(R.string.device_id), value = screenState.device.deviceId)
                InfoItem(title = stringResource(R.string.device_name), value = screenState.device.deviceName)
                InfoItem(title = stringResource(R.string.device_type), value = screenState.device.deviceType.name)
                InfoItem(title = stringResource(R.string.host), value = screenState.device.host)
                InfoItem(
                    title = stringResource(R.string.client_fingerprint),
                    value = screenState.device.clientCertificateFingerprint
                )
                InfoItem(
                    title = stringResource(R.string.server_fingerprint),
                    value = screenState.device.serverCertificateFingerprint
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .toggleable(
                    value = false,
                    onValueChange = {
                        // onEvent(DeviceConfigurationUiEvent.OnAwaitUnlockChanged(it))
                    },
                    role = Role.Checkbox
                )
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = false,
                onCheckedChange = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Await unlock requests",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка удаления
        Button(
            onClick = {
                onEvent(DeviceConfigurationUiEvent.RemoveDevice)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.delete),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.remove_device))
        }
    }
}

@Composable
private fun InfoItem(title: String, value: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}