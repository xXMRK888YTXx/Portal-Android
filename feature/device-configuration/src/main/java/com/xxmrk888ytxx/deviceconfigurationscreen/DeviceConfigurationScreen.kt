package com.xxmrk888ytxx.deviceconfigurationscreen

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.coreandroid.DefaultValidator
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.corecompose.MacAddressTransformation
import com.xxmrk888ytxx.corecompose.sharedUi.CenterAlignedTopAppBarWithBackArrow
import com.xxmrk888ytxx.deviceconfigurationscreen.component.AuthorizationParametersCard
import com.xxmrk888ytxx.deviceconfigurationscreen.component.BluetoothPairingWarningCard
import com.xxmrk888ytxx.deviceconfigurationscreen.component.DangerZoneCard
import com.xxmrk888ytxx.deviceconfigurationscreen.component.DeviceParametersCard
import com.xxmrk888ytxx.deviceconfigurationscreen.component.LoadingState
import com.xxmrk888ytxx.deviceconfigurationscreen.component.SectionTitle
import com.xxmrk888ytxx.deviceconfigurationscreen.component.SecurityAndMtlsCard
import com.xxmrk888ytxx.deviceconfigurationscreen.component.UnlockScenarioCard
import com.xxmrk888ytxx.deviceconfigurationscreen.component.isValidMacInput
import com.xxmrk888ytxx.deviceconfigurationscreen.dialog.CertificateDetailBottomSheet
import com.xxmrk888ytxx.deviceconfigurationscreen.dialog.EditValueDialog
import com.xxmrk888ytxx.deviceconfigurationscreen.dialog.RemoveDeviceBottomSheet
import com.xxmrk888ytxx.deviceconfigurationscreen.model.ActiveEditDialog
import com.xxmrk888ytxx.deviceconfigurationscreen.model.BottomSheetDialogState
import com.xxmrk888ytxx.deviceconfigurationscreen.model.CertDetailsInfo
import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationScreenSideEffect
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationUiEvent
import com.xxmrk888ytxx.deviceconfigurationscreen.model.ScreenState
import kotlinx.coroutines.flow.Flow
import com.xxmrk888ytxx.corecompose.R as ComposeR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceConfigurationScreen(
    screenState: ScreenState,
    onEvent: (DeviceConfigurationUiEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    val context = LocalContext.current
    HandleSideEffect<DeviceConfigurationScreenSideEffect>(sideEffect) { effect ->
        when (effect) {
            DeviceConfigurationScreenSideEffect.OpenBluetoothSettings -> {
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                context.startActivity(intent)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(),
        topBar = {
            if (screenState is ScreenState.DeviceInfo) {
                CenterAlignedTopAppBarWithBackArrow(
                    title = {
                        Text(
                            text = screenState.device.deviceName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
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
            contentKey = { it is ScreenState.DeviceInfo },
            label = "DeviceConfigScreenContent"
        ) { state ->
            when (state) {
                is ScreenState.DeviceInfo -> DeviceInfoState(state, onEvent)
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
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var activeDialog by remember { mutableStateOf<ActiveEditDialog?>(null) }
    var certDetails by remember { mutableStateOf<CertDetailsInfo?>(null) }

    val copyToClipboard: (String) -> Unit = { text ->
        clipboardManager.setText(AnnotatedString(text))
        Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bluetooth Pairing Warning (if needed)
        AnimatedVisibility(
            (screenState.device as? Device.BluetoothDevice)?.isPaired == false,
        ) {
            BluetoothPairingWarningCard(
                onOpenSettings = {
                    onEvent(DeviceConfigurationUiEvent.OpenBluetoothSettings)
                },
            )
        }

        // 1. Device Parameters Card (Name, Transport, IP, MAC)
        DeviceParametersCard(
            device = screenState.device,
            onEditName = { activeDialog = ActiveEditDialog.DEVICE_NAME },
            onEditHost = { activeDialog = ActiveEditDialog.HOST_IP },
            onEditMac = { activeDialog = ActiveEditDialog.WOL_MAC },
            onCopy = copyToClipboard
        )

        // 2. Security & mTLS / Device Identification Card
        val isWifi = screenState.device is Device.WifiDevice
        SectionTitle(
            icon = ComposeR.drawable.verified_user,
            title = if (isWifi) {
                stringResource(R.string.security_and_mtls)
            } else {
                stringResource(R.string.device_identification)
            }
        )

        SecurityAndMtlsCard(
            device = screenState.device,
            onShowInfo = { title, type, value, desc ->
                certDetails = CertDetailsInfo(title, type, value, desc)
            },
            onCopy = copyToClipboard
        )

        // 3. Unlock Scenario Card
        SectionTitle(
            icon = ComposeR.drawable.touch_app,
            title = stringResource(R.string.unlock_scenario)
        )

        UnlockScenarioCard(
            currentMethod = screenState.device.unlockMethod,
            isUnsafeUnlockMethodsDisabled = screenState.isUnsafeUnlockMethodsDisabled,
            onMethodChanged = { onEvent(DeviceConfigurationUiEvent.OnUnlockMethodChanged(it)) }
        )

        // 4. Authorization & Connection Parameters Card
        SectionTitle(
            icon = ComposeR.drawable.hub,
            title = stringResource(R.string.authorization_and_connection_parameters)
        )

        AuthorizationParametersCard(
            device = screenState.device,
            onUnlockOnlyWhenUnlockedChanged = {
                onEvent(DeviceConfigurationUiEvent.OnUnlockOnlyWhenScreenUnlockedChanged(it))
            },
            onAwaitUnlockChanged = {
                onEvent(DeviceConfigurationUiEvent.OnAwaitUnlockChanged(it))
            },
            onForwardToWearChanged = {
                onEvent(DeviceConfigurationUiEvent.OnForwardUnlockRequestsToWearChanged(it))
            },
            onSearchIpDynamicallyChanged = {
                onEvent(DeviceConfigurationUiEvent.OnSearchIpDynamicallyChanged(it))
            }
        )

        // 5. Danger Zone Card
        SectionTitle(
            icon = ComposeR.drawable.link_off,
            title = stringResource(R.string.danger_zone),
            isError = true
        )

        DangerZoneCard(
            isWifi = isWifi,
            onClick = { onEvent(DeviceConfigurationUiEvent.ShowRemoveDialog) }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Active Edit Dialog
    when (activeDialog) {
        ActiveEditDialog.DEVICE_NAME -> {
            EditValueDialog(
                title = stringResource(R.string.device_name),
                initialValue = screenState.device.deviceName,
                validator = { it.isNotBlank() },
                onDismiss = { activeDialog = null },
                onSave = {
                    onEvent(DeviceConfigurationUiEvent.OnDeviceNameChanged(it))
                    activeDialog = null
                }
            )
        }
        ActiveEditDialog.HOST_IP -> {
            if (screenState.device is Device.WifiDevice) {
                EditValueDialog(
                    title = stringResource(R.string.host),
                    initialValue = screenState.device.host,
                    keyboardType = KeyboardType.Uri,
                    validator = { it.isNotBlank() && DefaultValidator.isHostValid(it) },
                    onDismiss = { activeDialog = null },
                    onSave = {
                        onEvent(DeviceConfigurationUiEvent.OnHostChanged(it))
                        activeDialog = null
                    }
                )
            }
        }
        ActiveEditDialog.WOL_MAC -> {
            if (screenState.device is Device.WifiDevice) {
                EditValueDialog(
                    title = stringResource(R.string.wake_on_lan_mac_address),
                    initialValue = screenState.device.wolMacAddress ?: "",
                    keyboardType = KeyboardType.Ascii,
                    inputFilter = { it.isEmpty() || it.isValidMacInput() },
                    validator = { it.length == 12 },
                    visualTransformation = MacAddressTransformation(),
                    onDismiss = { activeDialog = null },
                    onSave = {
                        onEvent(DeviceConfigurationUiEvent.OnWakeOnLanMacAddressChanged(it))
                        activeDialog = null
                    }
                )
            }
        }
        null -> {}
    }

    // Certificate / UUID Details Modal Bottom Sheet
    certDetails?.let { details ->
        CertificateDetailBottomSheet(
            details = details,
            onDismiss = { certDetails = null },
            onCopy = copyToClipboard
        )
    }

    // Delete Device Confirmation Bottom Sheet
    if (screenState.bottomSheetDialogState is BottomSheetDialogState.DeleteDevice) {
        RemoveDeviceBottomSheet(
            onDismiss = {
                onEvent(DeviceConfigurationUiEvent.HideRemoveDialog)
            },
            onConfirm = {
                onEvent(DeviceConfigurationUiEvent.RemoveDevice)
            }
        )
    }
}
