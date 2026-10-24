package com.xxmrk888ytxx.deviceconfigurationscreen

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.coreandroid.DefaultValidator
import com.xxmrk888ytxx.coreandroid.formatToMacAddress
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.corecompose.MacAddressTransformation
import com.xxmrk888ytxx.corecompose.sharedUi.CenterAlignedTopAppBarWithBackArrow
import com.xxmrk888ytxx.deviceconfigurationscreen.model.BottomSheetDialogState
import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationScreenSideEffect
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationUiEvent
import com.xxmrk888ytxx.deviceconfigurationscreen.model.ScreenState
import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod
import kotlinx.coroutines.flow.Flow
import com.xxmrk888ytxx.corecompose.R as ComposeR

private enum class IdentifierType {
    SHA_256,
    UUID_V4
}

private data class CertDetailsInfo(
    val title: String,
    val type: IdentifierType,
    val value: String,
    val description: String? = null
)

private enum class ActiveEditDialog {
    DEVICE_NAME,
    HOST_IP,
    WOL_MAC
}

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

@Composable
private fun SectionTitle(
    icon: Int,
    title: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    val color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(start = 4.dp, top = 8.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = color
        )
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = color
        )
    }
}

@Composable
private fun DeviceParametersCard(
    device: Device,
    onEditName: () -> Unit,
    onEditHost: () -> Unit,
    onEditMac: () -> Unit,
    onCopy: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Icon, Name + Transport Pill, Edit Name Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            if (device is Device.WifiDevice) ComposeR.drawable.router else ComposeR.drawable.bluetooth
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = device.deviceName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = if (device is Device.WifiDevice) {
                                    stringResource(R.string.wifi_websockets)
                                } else {
                                    stringResource(R.string.bluetooth_rfcomm)
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onEditName,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = stringResource(R.string.device_name),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            // Network Parameters List
            if (device is Device.WifiDevice) {
                // IP Address Row
                ParameterRow(
                    label = stringResource(R.string.ip_address),
                    value = device.host,
                    onCopy = { onCopy(device.host) },
                    onEdit = onEditHost
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    thickness = 1.dp
                )

                // Wake-on-LAN MAC Address Row
                val wolMac = device.wolMacAddress
                val displayMac = wolMac?.formatToMacAddress() ?: stringResource(R.string.not_specified)
                ParameterRow(
                    label = stringResource(R.string.wake_on_lan_mac_address),
                    value = displayMac,
                    onCopy = wolMac?.let { { onCopy(it) } },
                    onEdit = onEditMac
                )
            } else if (device is Device.BluetoothDevice) {
                // Bluetooth MAC Address Row
                ParameterRow(
                    label = stringResource(R.string.mac_address),
                    value = device.macAddress,
                    onCopy = { onCopy(device.macAddress) },
                    onEdit = null
                )
            }
        }
    }
}

@Composable
private fun ParameterRow(
    label: String,
    value: String,
    onCopy: (() -> Unit)?,
    onEdit: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (onCopy != null) {
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(ComposeR.drawable.content_copy),
                        contentDescription = stringResource(R.string.copy_ip),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (onEdit != null) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = stringResource(R.string.edit_host),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityAndMtlsCard(
    device: Device,
    onShowInfo: (title: String, type: IdentifierType, value: String, description: String?) -> Unit,
    onCopy: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (device is Device.WifiDevice) {
                // 1. Блок: Сервер mTLS
                val serverTitle = stringResource(R.string.server_mtls)
                val serverCertModalTitle = stringResource(R.string.server_mtls_cert)
                val serverFp = device.serverCertificateFingerprint
                val serverDisplayFp = serverFp.truncateFingerprint()

                SecurityIdentifierItemCard(
                    icon = ComposeR.drawable.key,
                    title = serverTitle,
                    tag = stringResource(R.string.sha_256),
                    displayValue = serverDisplayFp,
                    fullValue = serverFp,
                    onInfoClick = {
                        onShowInfo(
                            serverCertModalTitle,
                            IdentifierType.SHA_256,
                            serverFp,
                            null
                        )
                    },
                    onCopyClick = { onCopy(serverFp) }
                )

                // 2. Блок: Клиент mTLS
                val clientTitle = stringResource(R.string.client_mtls)
                val clientCertModalTitle = stringResource(R.string.client_mtls_cert)
                val clientFp = device.clientCertificateFingerprint
                val clientDisplayFp = clientFp.truncateFingerprint()

                SecurityIdentifierItemCard(
                    icon = ComposeR.drawable.smartphone,
                    title = clientTitle,
                    tag = stringResource(R.string.sha_256),
                    displayValue = clientDisplayFp,
                    fullValue = clientFp,
                    onInfoClick = {
                        onShowInfo(
                            clientCertModalTitle,
                            IdentifierType.SHA_256,
                            clientFp,
                            null
                        )
                    },
                    onCopyClick = { onCopy(clientFp) }
                )
            }

            // 3. Блок: Device UUID (ИД устройства)
            val uuidTitle = stringResource(R.string.device_uuid)
            val uuidModalTitle = stringResource(R.string.device_id_title)
            val uuidDisplay = device.clientId.truncateUuid()

            SecurityIdentifierItemCard(
                icon = ComposeR.drawable.fingerprint,
                title = uuidTitle,
                tag = stringResource(R.string.uuid_v4),
                displayValue = uuidDisplay,
                fullValue = device.clientId,
                onInfoClick = {
                    onShowInfo(
                        uuidModalTitle,
                        IdentifierType.UUID_V4,
                        device.clientId,
                        null
                    )
                },
                onCopyClick = { onCopy(device.clientId) }
            )
        }
    }
}

@Composable
private fun SecurityIdentifierItemCard(
    icon: Int,
    title: String,
    tag: String,
    displayValue: String,
    fullValue: String?,
    onInfoClick: () -> Unit,
    onCopyClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Squircle icon container
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.padding(top = 1.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = displayValue,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Action Buttons: Info (ℹ️) and Copy (content_copy)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        painter = painterResource(ComposeR.drawable.info),
                        contentDescription = stringResource(R.string.certificate_details),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (onCopyClick != null) {
                    IconButton(
                        onClick = onCopyClick,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            painter = painterResource(ComposeR.drawable.content_copy),
                            contentDescription = stringResource(R.string.copy_fingerprint),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UnlockScenarioCard(
    currentMethod: UnlockMethod,
    isUnsafeUnlockMethodsDisabled: Boolean,
    onMethodChanged: (UnlockMethod) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Option 1: Confirmation Screen
        UnlockScenarioOption(
            icon = ComposeR.drawable.smartphone,
            title = stringResource(R.string.confirmation_screen),
            subtitle = stringResource(R.string.confirmation_screen_desc),
            isSelected = currentMethod is UnlockMethod.ConfirmationScreen,
            enabled = true,
            onClick = { onMethodChanged(UnlockMethod.ConfirmationScreen) }
        )

        // Option 2: Interactive Notification
        val isNotificationEnabled = !isUnsafeUnlockMethodsDisabled || !UnlockMethod.Notification.isUnsafe
        UnlockScenarioOption(
            icon = ComposeR.drawable.notifications_active,
            title = stringResource(R.string.notification),
            subtitle = stringResource(R.string.interactive_notification_desc),
            isSelected = currentMethod is UnlockMethod.Notification,
            enabled = isNotificationEnabled,
            onClick = { onMethodChanged(UnlockMethod.Notification) }
        )

        // Option 3: Automatic
        val isAutoEnabled = !isUnsafeUnlockMethodsDisabled || !UnlockMethod.Automatic.isUnsafe
        UnlockScenarioOption(
            icon = ComposeR.drawable.bolt,
            title = stringResource(R.string.automatically),
            subtitle = stringResource(R.string.auto_unlock_desc),
            isSelected = currentMethod is UnlockMethod.Automatic,
            enabled = isAutoEnabled,
            onClick = { onMethodChanged(UnlockMethod.Automatic) }
        )
    }
}

@Composable
private fun UnlockScenarioOption(
    icon: Int,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = null,
                enabled = enabled
            )
        }
    }
}

@Composable
private fun AuthorizationParametersCard(
    device: Device,
    onUnlockOnlyWhenUnlockedChanged: (Boolean) -> Unit,
    onAwaitUnlockChanged: (Boolean) -> Unit,
    onForwardToWearChanged: (Boolean) -> Unit,
    onSearchIpDynamicallyChanged: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // Toggle 1: Only Unlocked Phone
            val (title, description) = if (device.unlockMethod is UnlockMethod.Automatic) {
                Pair(
                    stringResource(R.string.unlock_only_when_unlocked),
                    stringResource(R.string.unlock_only_when_unlocked_description)
                )
            } else {
                Pair(
                    stringResource(R.string.show_unlock_request_only_when_unlocked),
                    stringResource(R.string.show_unlock_request_only_when_unlocked_description)
                )
            }

            SwitchItemRow(
                icon = ComposeR.drawable.fingerprint,
                title = title,
                description = description,
                isChecked = device.showUnlockScreenOrUnlockOnlyWhenScreenUnlocked,
                onCheckedChange = onUnlockOnlyWhenUnlockedChanged
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            // Toggle 2: Await Unlock Requests
            SwitchItemRow(
                icon = ComposeR.drawable.memory,
                title = stringResource(R.string.await_unlock_requests),
                description = stringResource(R.string.await_unlock_requests_description),
                isChecked = device.awaitUnlockRequests,
                onCheckedChange = onAwaitUnlockChanged
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            // Toggle 3: Sync with Wear OS
            SwitchItemRow(
                icon = ComposeR.drawable.watch,
                title = stringResource(R.string.forward_unlock_requests_to_wear),
                description = stringResource(R.string.forward_unlock_requests_to_wear_description),
                isChecked = device.forwardUnlockRequestsToWear,
                onCheckedChange = onForwardToWearChanged
            )

            // Toggle 4: Dynamic IP mDNS (for WifiDevice)
            if (device is Device.WifiDevice) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    thickness = 1.dp
                )

                SwitchItemRow(
                    icon = ComposeR.drawable.router,
                    title = stringResource(R.string.search_ip_dynamically),
                    description = stringResource(R.string.search_ip_dynamically_description),
                    isChecked = device.searchIpDynamically,
                    onCheckedChange = onSearchIpDynamicallyChanged
                )
            }
        }
    }
}

@Composable
private fun SwitchItemRow(
    icon: Int,
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = isChecked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = null
        )
    }
}

@Composable
private fun DangerZoneCard(
    isWifi: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(ComposeR.drawable.link_off),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(R.string.unpair_device),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = if (isWifi) {
                        stringResource(R.string.unpair_device_desc)
                    } else {
                        stringResource(R.string.unpair_device_bluetooth_desc)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EditValueDialog(
    title: String,
    initialValue: String,
    validator: (String) -> Boolean,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    inputFilter: (String) -> Boolean = { true },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var textValue by rememberSaveable(initialValue) { mutableStateOf(initialValue) }
    val isValid = remember(textValue) { validator(textValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            OutlinedTextField(
                value = textValue,
                onValueChange = {
                    if (inputFilter(it)) {
                        textValue = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = visualTransformation,
                shape = RoundedCornerShape(14.dp),
                isError = !isValid
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(textValue) },
                enabled = isValid
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CertificateDetailBottomSheet(
    details: CertDetailsInfo,
    onDismiss: () -> Unit,
    onCopy: (String) -> Unit
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with verified icon, title, type badge, and close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(ComposeR.drawable.verified_user),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Column {
                        Text(
                            text = details.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    if (details.type == IdentifierType.UUID_V4) R.string.uuid_v4
                                    else R.string.sha_256
                                ),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            // Content
            val isUuid = details.type == IdentifierType.UUID_V4
            val label = if (isUuid) {
                stringResource(R.string.full_uuid)
            } else {
                stringResource(R.string.full_fingerprint)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = details.value,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (details.description != null) {
                    Text(
                        text = details.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Buttons: Copy and Close
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onCopy(details.value) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        painter = painterResource(ComposeR.drawable.content_copy),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUuid) stringResource(R.string.copy_uuid) else stringResource(R.string.copy_fingerprint),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.close),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun BluetoothPairingWarningCard(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.bluetooth_disabled),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.pairing_required),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Text(
                text = stringResource(R.string.pairing_required_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.go_to_settings))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveDeviceBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.delete),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.remove_device_bottom_sheet),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.are_you_sure_you_want_to_remove_this_device_this_action_cannot_be_undone),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(stringResource(R.string.remove))
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
fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

private fun String.truncateFingerprint(): String {
    if (length <= 20) return this
    val parts = split(":")
    return if (parts.size >= 8) {
        "${parts.take(4).joinToString(":")}...${parts.takeLast(3).joinToString(":")}"
    } else {
        "${take(11)}...${takeLast(8)}"
    }
}

private fun String.truncateUuid(): String {
    if (length <= 16) return this
    return "${take(8)}...${takeLast(8)}"
}

fun String.isValidMacInput(): Boolean {
    val cleanInput = this.replace(":", "")
    val allowedChars = "0123456789abcdefABCDEF"
    return cleanInput.all { it in allowedChars } && cleanInput.length <= 12
}

fun String.isMacComplete(): Boolean {
    return this.replace(":", "").length == 12
}
