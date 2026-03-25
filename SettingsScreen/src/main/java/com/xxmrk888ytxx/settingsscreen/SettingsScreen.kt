package com.xxmrk888ytxx.settingsscreen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
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
                    title = "Biometric Unlock",
                    subtitle = "Use fingerprint or face to unlock",
                    iconRes = R.drawable.fingerprint,
                    checked = screenState.isBiometricProtectionEnabled,
                    onCheckedChange = { isChecked ->
                        // Отправляем event во ViewModel для обновления состояния
                        // onEvent(SettingsScreenEvent.OnBiometricToggle(isChecked))
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
    subtitle: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Используем toggleable вместо clickable для правильной семантики Switch
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            )
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

        Switch(
            checked = checked,
            // Передаем null, так как клик обрабатывается на уровне Row через toggleable
            onCheckedChange = null
        )
    }
}