package com.xxmrk888ytxx.onboardingscreen

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.xxmrk888ytxx.coreandroid.AvatarLink
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenSideEffect
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenUiEvent
import com.xxmrk888ytxx.onboardingscreen.model.ScreenState
import kotlinx.coroutines.flow.Flow

@Composable
fun OnboardingScreen(
    screenState: ScreenState,
    onEvent: (OnboardingScreenUiEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {


    val requestPermissionContract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        onEvent(OnboardingScreenUiEvent.UpdatePermissionState)
    }
    val pagerState = rememberPagerState(pageCount = { 3 })
    HandleSideEffect<OnboardingScreenSideEffect>(sideEffect) { effect ->
        fastDebugLog(effect)
        when(effect) {
            OnboardingScreenSideEffect.NextPage -> pagerState.animateScrollToPage(pagerState.currentPage + 1)
            OnboardingScreenSideEffect.RequestNearbyDevicesPermission -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionContract.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }

            OnboardingScreenSideEffect.RequestNotificationPermission -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionContract.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        bottomBar = {
            OnboardingBottomBar(
                pagerState = pagerState,
                isNextEnabled = when (pagerState.currentPage) {
                    0 -> screenState.isTosAccepted
                    else -> true
                },
                onNextClick = {
                    if (pagerState.canScrollForward) onEvent(OnboardingScreenUiEvent.NextPage)
                    else onEvent(OnboardingScreenUiEvent.FinishOnboarding)
                }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> AppInfoPage(
                    isTosAccepted = screenState.isTosAccepted,
                    onTosChanged = { onEvent(OnboardingScreenUiEvent.TosAcceptedChanged(it)) },
                    onTosClick = { onEvent(OnboardingScreenUiEvent.OpenTOSLink) },
                    onPrivacyClick = { onEvent(OnboardingScreenUiEvent.OpenPrivacyPolicyLink) }
                )
                1 -> OpenSourcePage(
                    onAndroidRepoClick = { onEvent(OnboardingScreenUiEvent.OpenAndroidSourceCode) },
                    onDesktopRepoClick = { onEvent(OnboardingScreenUiEvent.OpenPCSourceCode) },
                    onAndroidDevClick = { onEvent(OnboardingScreenUiEvent.OpenAndroidDevelopGithub) },
                    onPCDevClick = { onEvent(OnboardingScreenUiEvent.OpenPCADeveloperGithub) },
                )
                2 -> PermissionsPage(
                    isNotificationGranted = screenState.isNotificationGranted,
                    onRequestNotification = { onEvent(OnboardingScreenUiEvent.RequestNotificationPermission) },
                    isNearbyDevicesGranted = screenState.isNearbyDevicesGranted,
                    onRequestNearbyDevices = { onEvent(OnboardingScreenUiEvent.RequestNearbyDevicesPermission) },
                    isOverlayGranted = screenState.isOverlayGranted,
                    onRequestOverlay = { onEvent(OnboardingScreenUiEvent.RequestOverlayPermission) },
                    isIgnoreBatteryOptimizationsGranted = screenState.isIgnoreBatteryOptimizationsGranted,
                    onRequestIgnoreBatteryOptimizations = { onEvent(OnboardingScreenUiEvent.RequestIgnoreBatteryOptimization) },
                )
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onEvent(OnboardingScreenUiEvent.UpdatePermissionState)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}


@Composable
fun AppInfoPage(
    isTosAccepted: Boolean,
    onTosChanged: (Boolean) -> Unit,
    onTosClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.portal),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.welcome_to_portal),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.app_open_description),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))

        val linkStyles = TextLinkStyles(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        )

        val annotatedString = buildAnnotatedString {
            append(stringResource(R.string.i_agree_to_the))

            withLink(
                LinkAnnotation.Clickable(
                    tag = "TOS",
                    styles = linkStyles,
                    linkInteractionListener = { onTosClick() }
                )
            ) {
                append(stringResource(R.string.terms_of_use))
            }

            append(stringResource(R.string.and))

            withLink(
                LinkAnnotation.Clickable(
                    tag = "PRIVACY",
                    styles = linkStyles,
                    linkInteractionListener = { onPrivacyClick() }
                )
            ) {
                append(stringResource(R.string.privacy_policy))
            }
        }

        Card(
            onClick = { onTosChanged(!isTosAccepted) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Switch(
                    checked = isTosAccepted,
                    onCheckedChange = { onTosChanged(it) }
                )
            }
        }
    }
}

@Composable
fun OpenSourcePage(
    onAndroidRepoClick: () -> Unit,
    onDesktopRepoClick: () -> Unit,
    onAndroidDevClick: () -> Unit,
    onPCDevClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Section
        Image(
            painter = painterResource(id = R.drawable.public_),
            contentDescription = "Open Source Logo",
            modifier = Modifier
                .size(80.dp)
                .padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.free_and_open_source),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.portal_is_a_completely_open_source_project_gpl_3_0_no_hidden_in_app_purchases_paid_limitations_or_annoying_ads_the_source_code_and_developer_contacts_are_waiting_for_you_below),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Repositories Section
        SectionTitle(title = stringResource(R.string.source_code))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column {
                RepositoryItem(
                    title = stringResource(R.string.portal_mobile_client),
                    subtitle = stringResource(R.string.android_application_source_code),
                    icon = R.drawable.android,
                    onClick = onAndroidRepoClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                RepositoryItem(
                    title = stringResource(R.string.portal_desktop_client),
                    subtitle = stringResource(R.string.pc_client_source_code),
                    icon = R.drawable.desktop,
                    onClick = onDesktopRepoClick
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Developers Section
        SectionTitle(title = stringResource(R.string.developers_team))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column {
                GithubProfileItem(
                    title = stringResource(R.string.xxmrk888ytxx),
                    subtitle = stringResource(R.string.android_developer),
                    avatarUrl = AvatarLink.ANDROID_DEVELOPER_LINK,
                    onClick = onAndroidDevClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                GithubProfileItem(
                    title = stringResource(R.string.xxkoksmanxx),
                    subtitle = stringResource(R.string.pc_developer),
                    avatarUrl = AvatarLink.PC_DEVELOPER_LINK,
                    onClick = onPCDevClick
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, start = 8.dp)
    )
}

@Composable
fun RepositoryItem(
    title: String,
    subtitle: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "Repository Icon",
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
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
                fontWeight = FontWeight.Medium,
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
fun PermissionsPage(
    isNotificationGranted: Boolean,
    onRequestNotification: () -> Unit,
    isNearbyDevicesGranted: Boolean,
    onRequestNearbyDevices: () -> Unit,
    isOverlayGranted: Boolean,
    isIgnoreBatteryOptimizationsGranted: Boolean,
    onRequestOverlay: () -> Unit,
    onRequestIgnoreBatteryOptimizations: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.security),
            contentDescription = "Permissions",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.required_permissions),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.for_the_app_to_work_correctly_you_must_provide_the_following_permissions_you_have_the_right_to_refuse_to_grant_all_or_some_permissions_but_in_this_case_some_functions_of_the_application_may_not_be_available),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PermissionItem(
                iconRes = R.drawable.notification,
                title = stringResource(R.string.notifications),
                description = stringResource(R.string.it_is_used_to_display_pc_unlock_notifications_and_background_work),
                isGranted = isNotificationGranted,
                onRequest = onRequestNotification
            )

            PermissionItem(
                iconRes = R.drawable.nearby,
                title = stringResource(R.string.nearby_devices),
                description = stringResource(R.string.this_permission_is_required_by_the_app_if_you_use_bluetooth_to_unlock_your_device),
                isGranted = isNearbyDevicesGranted,
                onRequest = onRequestNearbyDevices
            )

            PermissionItem(
                iconRes = R.drawable.open_in_full,
                title = stringResource(R.string.display_over_other_apps),
                description = stringResource(R.string.this_permission_is_required_to_display_device_unlock_prompts_on_top_of_all_windows_this_allows_you_to_instantly_grant_or_deny_access_without_unlocking_your_phone_or_opening_the_app),
                isGranted = isOverlayGranted,
                onRequest = onRequestOverlay
            )

            PermissionItem(
                iconRes = R.drawable.battery,
                title = stringResource(R.string.permission_to_ignore_battery_optimizations),
                description = stringResource(R.string.to_ensure_all_features_work_correctly_in_the_background_please_disable_battery_optimization_for_this_app),
                isGranted = isIgnoreBatteryOptimizationsGranted,
                onRequest = onRequestIgnoreBatteryOptimizations
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun PermissionItem(
    iconRes: Int,
    title: String,
    description: String,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    val cardAlpha = if (isGranted) 0.6f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (isGranted) {
                Text(
                    text = stringResource(R.string.granted),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Button(
                    onClick = onRequest,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(stringResource(R.string.allow))
                }
            }
        }
    }
}


@Composable
fun OnboardingBottomBar(
    pagerState: PagerState,
    isNextEnabled: Boolean,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .windowInsetsPadding(WindowInsets.navigationBars),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                )
            }
        }

        Button(
            onClick = onNextClick,
            enabled = isNextEnabled
        ) {
            Text(if (!pagerState.canScrollForward) stringResource(R.string.get_started) else stringResource(
                R.string.next
            ))
        }
    }
}