package com.xxmrk888ytxx.settingsscreen

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.settingsscreen.model.BottomSheetState
import com.xxmrk888ytxx.settingsscreen.model.ScreenState
import com.xxmrk888ytxx.settingsscreen.model.SettingsScreenEvent
import com.xxmrk888ytxx.settingsscreen.model.SettingsScreenSideEffect
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    screenState: ScreenState,
    onEvent: (SettingsScreenEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    HandleSideEffect<SettingsScreenSideEffect>(sideEffect) {}
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        contentWindowInsets = WindowInsets()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // Security Section
            SettingsSection(title = stringResource(R.string.security)) {

                SettingsSwitchItem(
                    title = stringResource(R.string.biometric_unlock),
                    subtitle = stringResource(R.string.use_fingerprint_or_face_to_unlock),
                    iconRes = R.drawable.fingerprint,
                    checked = screenState.isBiometricProtectionEnabled,
                    onCheckedChange = { isChecked ->
                        onEvent(SettingsScreenEvent.OnBiometricProtectionStateChanged(isChecked))
                    }
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.allow_password_unlock),
                    subtitle = stringResource(R.string.can_be_used_as_an_alternative_to_biometric_authentication),
                    iconRes = R.drawable.fingerprint,
                    checked = screenState.isAdditionalPasswordAuthEnabled,
                    enabled = screenState.isBiometricProtectionEnabled,
                    onCheckedChange = { isChecked ->
                        onEvent(SettingsScreenEvent.OnAdditionalPasswordAuthStateChanged(isChecked))
                    }
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.unpair_devices_if_the_biometric_environment_has_changed),
                    subtitle = stringResource(R.string.if_a_new_fingerprint_is_added_or_an_old_one_is_deleted_all_paired_devices_will_be_removed),
                    iconRes = R.drawable.fingerprint,
                    checked = screenState.isRemovePairedClientsIfBiometricEnvironmentChangedEnabled,
                    enabled = screenState.isBiometricProtectionEnabled,
                    onCheckedChange = { isChecked ->
                        onEvent(SettingsScreenEvent.OnRemovePairedClientsIfBiometricEnvironmentStateChanged(isChecked))
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About App Section
            SettingsSection(title = stringResource(R.string.about_app)) {
                SettingsItem(
                    title = stringResource(R.string.terms_of_use),
                    iconRes = R.drawable.ic_terms,
                    onClick = { /* onEvent(SettingsScreenEvent.OnTermsClick) */ }
                )
                SettingsItem(
                    title = stringResource(R.string.privacy_policy),
                    iconRes = R.drawable.ic_privacy,
                    onClick = { /* onEvent(SettingsScreenEvent.OnPrivacyClick) */ }
                )
                SettingsItem(
                    title = stringResource(R.string.source_code),
                    subtitle = stringResource(R.string.github_repository),
                    iconRes = R.drawable.code,
                    onClick = { /* onEvent(SettingsScreenEvent.OnSourceCodeClick) */ }
                )
                SettingsItem(
                    title = stringResource(R.string.android_app_developer),
                    subtitle = stringResource(R.string.xxmrk888ytxx),
                    iconRes = R.drawable.ic_developer,
                    onClick = { /* onEvent(SettingsScreenEvent.OnDeveloperClick) */ }
                )
                SettingsItem(
                    title = stringResource(R.string.pc_client_developer),
                    subtitle = stringResource(R.string.xxkoksmenxx),
                    iconRes = R.drawable.ic_developer,
                    onClick = { /* onEvent(SettingsScreenEvent.OnDeveloperClick) */ }
                )
                SettingsItem(
                    title = stringResource(R.string.app_version),
                    subtitle = screenState.appVersion,
                    iconRes = R.drawable.ic_version,
                    onClick = { /* Обычно на версию не кликают, но можно добавить пасхалку */ }
                )
                SettingsItem(
                    title = stringResource(R.string.app_logs),
                    iconRes = R.drawable.ic_version,
                    onClick = { onEvent(SettingsScreenEvent.OnLogsClick) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    when(screenState.bottomSheetState) {
        is BottomSheetState.ConfirmSecurityChangesDialog -> ConfirmSecurityChangesDialog(
            isForEnablingSetting = screenState.bottomSheetState.isForEnablingSetting,
            onDismiss = { onEvent(SettingsScreenEvent.HideBottomSheet) },
            onConfirm = { onEvent(SettingsScreenEvent.ConfirmSecurityChanges(screenState.bottomSheetState.actionAfterConfirm)) }
        )
        BottomSheetState.None -> {}
    }
}


@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    @DrawableRes iconRes: Int,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    @DrawableRes iconRes: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true // Новый флаг
) {
    val alpha by animateFloatAsState(if (enabled) 1f else 0.38f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                enabled = enabled,
                onValueChange = onCheckedChange,
                role = Role.Switch
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .alpha(alpha)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmSecurityChangesDialog(
    isForEnablingSetting: Boolean,
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
                painter = painterResource(R.drawable.security),
                contentDescription = null,
                tint = if (isForEnablingSetting) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.attention),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isForEnablingSetting)
                    stringResource(R.string.if_you_disable_this_setting_in_future_all_your_paired_devices_will_be_deleted_do_you_still_want_to_enable_it_note_enabling_this_setting_will_not_affect_your_paired_devices)
                    else stringResource(R.string.for_security_reasons_disabling_this_setting_will_result_in_the_removal_of_all_paired_devices_do_you_still_want_to_disable_it),
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
                    containerColor = if (isForEnablingSetting) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    contentColor = if (isForEnablingSetting) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onError
                )
            ) {
                Text(if (isForEnablingSetting) stringResource(R.string.enable) else stringResource(R.string.disable))
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