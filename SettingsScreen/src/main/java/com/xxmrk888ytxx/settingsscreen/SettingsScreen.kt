package com.xxmrk888ytxx.settingsscreen

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.oss.licenses.v2.OssLicensesMenuActivity
import com.xxmrk888ytxx.coreandroid.AvatarLink
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.corecompose.theme.AppSeedColors
import com.xxmrk888ytxx.settingsscreen.model.BottomSheetState
import com.xxmrk888ytxx.settingsscreen.model.ScreenState
import com.xxmrk888ytxx.settingsscreen.model.SettingsScreenEvent
import com.xxmrk888ytxx.settingsscreen.model.SettingsScreenEvent.*
import com.xxmrk888ytxx.settingsscreen.model.SettingsScreenSideEffect
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    screenState: ScreenState,
    onEvent: (SettingsScreenEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    val context = LocalContext.current
    HandleSideEffect<SettingsScreenSideEffect>(sideEffect) { effect ->
        when(effect) {
            SettingsScreenSideEffect.OpenOpenSourceLicenses -> {
                val intent = Intent(context, OssLicensesMenuActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
    var isLogsVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var versionClickCounter by rememberSaveable {
        mutableIntStateOf(0)
    }
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
                    enabled = screenState.isBiometricProtectionEnabled || screenState.isBiometricAuthAvailable,
                    errorText = if (!screenState.isBiometricAuthAvailable) stringResource(R.string.biometrics_is_not_configured_or_is_unavailable) else null,
                    onCheckedChange = { isChecked ->
                        onEvent(SettingsScreenEvent.OnBiometricProtectionStateChanged(isChecked))
                    }
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.prohibit_insecure_unlock_methods),
                    subtitle = stringResource(R.string.the_automatically_unlock_method_will_be_disabled_for_devices_where_this_unlock_method_is_currently_set_it_will_be_changed_to_notification),
                    iconRes = R.drawable.insecure,
                    checked = screenState.isUnsafeUnlockTypesDisabled,
                    isExpandable = true,
                    onCheckedChange = { isChecked ->
                        onEvent(SettingsScreenEvent.OnChangeUnsafeUnlockTypesState(isChecked))
                    }
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.unpair_devices_if_the_biometric_environment_has_changed),
                    subtitle = stringResource(R.string.if_a_new_fingerprint_is_added_or_an_old_one_is_deleted_all_paired_devices_will_be_removed),
                    iconRes = R.drawable.encrypted,
                    checked = screenState.isRemovePairedClientsIfBiometricEnvironmentChangedEnabled,
                    enabled = screenState.isBiometricProtectionEnabled,
                    onCheckedChange = { isChecked ->
                        onEvent(SettingsScreenEvent.OnRemovePairedClientsIfBiometricEnvironmentStateChanged(isChecked))
                    }
                )

                SettingsSwitchItem(
                    title = stringResource(R.string.allow_password_unlock),
                    subtitle = stringResource(R.string.can_be_used_as_an_alternative_to_biometric_authentication),
                    iconRes = R.drawable.password,
                    checked = screenState.isAdditionalPasswordAuthEnabled,
                    enabled = screenState.isBiometricProtectionEnabled,
                    onCheckedChange = { isChecked ->
                        onEvent(SettingsScreenEvent.OnAdditionalPasswordAuthStateChanged(isChecked))
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            SettingsSection(stringResource(R.string.customization)) {
                SettingsItem(
                    title = stringResource(R.string.theme),
                    iconRes = R.drawable.palette
                ) {
                    onEvent(SettingsScreenEvent.OnThemeClicked)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))


            // About App Section
            SettingsSection(title = stringResource(R.string.about_app)) {
                SettingsItem(
                    title = stringResource(R.string.terms_of_use),
                    iconRes = R.drawable.ic_terms,
                    onClick = { onEvent(SettingsScreenEvent.OnTermsClicked) }
                )
                SettingsItem(
                    title = stringResource(R.string.privacy_policy),
                    iconRes = R.drawable.ic_privacy,
                    onClick = { onEvent(SettingsScreenEvent.OnPrivacyClicked) }
                )
                SettingsItem(
                    title = stringResource(R.string.source_code_android),
                    subtitle = stringResource(R.string.github_repository),
                    iconRes = R.drawable.code,
                    onClick = { onEvent(SettingsScreenEvent.OnAndroidSourceCodeClick) }
                )
                SettingsItem(
                    title = stringResource(R.string.source_code_pc),
                    subtitle = stringResource(R.string.github_repository),
                    iconRes = R.drawable.code,
                    onClick = { onEvent(SettingsScreenEvent.OnPCSourceCodeClick) }
                )
                GithubProfileItem(
                    title = stringResource(R.string.android_app_developer),
                    subtitle = stringResource(R.string.xxmrk888ytxx),
                    avatarUrl = AvatarLink.ANDROID_DEVELOPER_LINK,
                    onClick = { onEvent(SettingsScreenEvent.OnAndroidDeveloperClick) }
                )
                GithubProfileItem(
                    title = stringResource(R.string.pc_client_developer),
                    subtitle = stringResource(R.string.xxkoksmenxx),
                    avatarUrl = AvatarLink.PC_DEVELOPER_LINK,
                    onClick = { onEvent(SettingsScreenEvent.OnPCDeveloperClicked) }
                )
                SettingsItem(
                    title = stringResource(R.string.open_source_licenses),
                    iconRes = R.drawable.code,
                    onClick = { onEvent(SettingsScreenEvent.OpenOpenSourceLicenses) }
                )
                SettingsItem(
                    title = stringResource(R.string.app_version),
                    subtitle = screenState.appVersion,
                    iconRes = R.drawable.ic_version,
                    onClick = {
                        versionClickCounter += 1
                        if (versionClickCounter == 5) {
                            isLogsVisible = true
                        }
                    }
                )
                AnimatedVisibility(isLogsVisible) {
                    SettingsItem(
                        title = stringResource(R.string.app_logs),
                        iconRes = R.drawable.ic_version,
                        onClick = { onEvent(SettingsScreenEvent.OnLogsClick) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    when(screenState.bottomSheetState) {
        is BottomSheetState.ConfirmSecurityChangesDialog -> ConfirmSecurityChangesDialog(
            isForEnablingSetting = screenState.bottomSheetState.isForEnablingSetting,
            onDismiss = { onEvent(SettingsScreenEvent.HideBottomSheet) },
            onConfirm = { onEvent(ConfirmSecurityChanges(screenState.bottomSheetState.actionAfterConfirm)) }
        )
        BottomSheetState.None -> {}
        is BottomSheetState.SelectThemeDialog -> SelectThemeDialog(
            onDismiss = { onEvent(SettingsScreenEvent.HideBottomSheet) },
            onThemeSelected = {
                onEvent(SettingsScreenEvent.OnThemeColorSelected(it))
            },
            selectedColor = screenState.bottomSheetState.selectedThemeColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SelectThemeDialog(
    selectedColor: Color?, // null = System, Color.Unspecified = Random
    onDismiss: () -> Unit,
    onThemeSelected: (Color?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.choose_a_theme),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Standard Options: System and Random
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // System Button
                val isSystemSelected = selectedColor == null
                val systemContainerColor = if (isSystemSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                val systemContentColor = if (isSystemSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.primary

                OutlinedButton(
                    onClick = {
                        onThemeSelected(null)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = systemContainerColor,
                        contentColor = systemContentColor
                    ),
                    border = if (isSystemSelected) null else ButtonDefaults.outlinedButtonBorder()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.android),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.system))
                }

                // Random Button
                val isRandomSelected = selectedColor == Color.Unspecified
                val randomContainerColor = if (isRandomSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                val randomContentColor = if (isRandomSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.primary

                OutlinedButton(
                    onClick = {
                        onThemeSelected(Color.Unspecified)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = randomContainerColor,
                        contentColor = randomContentColor
                    ),
                    border = if (isRandomSelected) null else ButtonDefaults.outlinedButtonBorder()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.shuffle),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.random))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.colors),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Colors Grid using FlowRow
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AppSeedColors.allColors.forEach { color ->
                    val isColorSelected = selectedColor == color

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable {
                                onThemeSelected(color)
                                onDismiss()
                            }
                            .border(
                                width = if (isColorSelected) 2.dp else 1.dp,
                                color = if (isColorSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isColorSelected) {
                            Icon(
                                painter = painterResource(R.drawable.check),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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
fun GithubProfileItem(
    title: String,
    avatarUrl: String?,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 10.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val placeholder = painterResource(id = R.drawable.ic_developer)

        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Github Avatar",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
                fallback = placeholder
            )
        }

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
    isExpandable: Boolean = false,
    collapsedMaxLines: Int = 2,
    errorText: String? = null,
    enabled: Boolean = true
) {
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.38f,
        label = "alpha_anim"
    )

    val isError = remember(errorText) { errorText != null }

    var isExpanded by remember { mutableStateOf(false) }
    var hasVisualOverflow by remember { mutableStateOf(false) }

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
                .animateContentSize()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (isExpandable && !isExpanded) collapsedMaxLines else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textLayoutResult ->
                        // Проверяем, обрезался ли текст
                        if (isExpandable && !isExpanded) {
                            hasVisualOverflow = textLayoutResult.hasVisualOverflow
                        }
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )

                if (isExpandable && (hasVisualOverflow || isExpanded)) {
                    Text(
                        text = if (isExpanded) stringResource(R.string.show_less) else stringResource(
                            R.string.read_more
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(top = 4.dp, end = 8.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { isExpanded = !isExpanded }
                            )
                            .padding(vertical = 4.dp)
                    )
                }
            }

            if (errorText != null) {
                Text(
                    text = errorText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp, end = 8.dp)
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