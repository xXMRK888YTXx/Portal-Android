package com.xxmrk888ytxx.deviceconfigurationscreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.coreandroid.DefaultValidator
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.corecompose.sharedUi.CenterAlignedTopAppBarWithBackArrow
import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationUiEvent
import com.xxmrk888ytxx.deviceconfigurationscreen.model.ScreenState
import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod
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
                .padding(paddingValues),
            contentKey = { it is ScreenState.DeviceInfo }
        ) { state ->
            when (state) {
                is ScreenState.DeviceInfo -> DeviceInfoState(state, onEvent)
                ScreenState.Loading -> LoadingState()
            }
        }
    }
}

@Composable
private fun SwitchSettingCard(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .toggleable(
                    value = isChecked,
                    onValueChange = onCheckedChange,
                    role = Role.Switch
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = null
            )
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
                InfoItem(
                    title = stringResource(R.string.device_id),
                    value = screenState.device.deviceId
                )
                InfoItem(
                    title = stringResource(R.string.device_name),
                    value = screenState.device.deviceName
                )
                InfoItem(
                    title = stringResource(R.string.device_type),
                    value = when(screenState.device) {
                        is Device.WifiDevice -> stringResource(R.string.wifi)
                        is Device.BluetoothDevice -> stringResource(R.string.bluetooth)
                    }
                )
                if (screenState.device is Device.BluetoothDevice) {
                    InfoItem(
                        title = stringResource(R.string.mac_address),
                        value = screenState.device.macAddress
                    )
                }

                if (screenState.device is Device.WifiDevice) {
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
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        if (screenState.device is Device.WifiDevice) {
            EditableIpCard(
                currentIp = screenState.device.host,
                onIpSaved = { newIp ->
                    onEvent(DeviceConfigurationUiEvent.OnHostChanged(newIp))
                }
            )
        }

        UnlockMethodSelector(
            currentMethod = UnlockMethod.Automatic(false),
            onMethodChanged = { newMethod ->
                //onEvent(DeviceConfigurationUiEvent.OnUnlockMethodChanged(newMethod))
            },
        )

//        AnimatedVisibility(visible = currentMethod == UnlockMethod.AUTOMATIC) {
//            SwitchSettingCard(
//                title = stringResource(R.string.unlock_only_when_the_screen_is_on),
//                description = stringResource(R.string.if_your_phone_screen_is_locked_your_pc_will_only_be_unlocked_once_your_phone_has_been_unlocked),
//                isChecked = unlockOnlyWhenScreenUnlocked,
//                onCheckedChange = onUnlockOnlyWhenScreenUnlockedChanged
//            )
//        }

        SwitchSettingCard(
            title = stringResource(R.string.await_unlock_requests),
            description = stringResource(R.string.await_unlock_requests_description),
            isChecked = screenState.device.awaitUnlockRequests,
            onCheckedChange = {
                onEvent(DeviceConfigurationUiEvent.OnAwaitUnlockChanged(it))
            }
        )

        if (screenState.device is Device.WifiDevice) {
            SwitchSettingCard(
                title = stringResource(R.string.search_ip_dynamically),
                description = stringResource(R.string.search_ip_dynamically_description),
                isChecked = screenState.device.searchIpDynamically,
                onCheckedChange = {
                    onEvent(DeviceConfigurationUiEvent.OnSearchIpDynamicallyChanged(it))
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

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
private fun EditableIpCard(
    currentIp: String,
    onIpSaved: (String) -> Unit
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var ipValue by rememberSaveable(currentIp) { mutableStateOf(currentIp) }
    val isValidIp = remember(ipValue) {
        DefaultValidator.isHostValid(ipValue)
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedContent(
            targetState = isEditing,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
            },
            label = "EditIpAnimation"
        ) { editing ->
            if (editing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.host),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = ipValue,
                        onValueChange = { ipValue = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        shape = MaterialTheme.shapes.medium,
                        isError = !isValidIp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                ipValue = currentIp
                                isEditing = false
                            }
                        ) {
                            Text(text = stringResource(android.R.string.cancel))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                onIpSaved(ipValue)
                                isEditing = false
                            },
                            enabled = ipValue.isNotBlank() && ipValue.isNotEmpty() && ipValue != currentIp && isValidIp
                        ) {
                            Text(text = stringResource(R.string.save))
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { isEditing = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.host),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentIp,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        painterResource(R.drawable.edit),
                        contentDescription = stringResource(R.string.edit_host),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UnlockMethodSelector(
    currentMethod: UnlockMethod,
    onMethodChanged: (UnlockMethod) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Unlock method",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UnlockMethod.entries.forEach { method ->

                // Сравниваем именно классы, чтобы игнорировать внутренние параметры data class
                val isSelected = currentMethod::class == method::class

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            onMethodChanged(method)
                        }
                    },
                    label = {
                        Text(
                            text = getUnlockMethodName(method),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun getUnlockMethodName(method: UnlockMethod): String {
    return when (method) {
        is UnlockMethod.Automatic -> stringResource(R.string.automatically)
        is  UnlockMethod.ConfirmationScreen -> stringResource(R.string.notification)
        is UnlockMethod.Notification -> stringResource(R.string.confirmation_screen)
    }
}

@Composable
fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}