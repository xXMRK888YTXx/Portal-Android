package com.xxmrk888ytxx.portal.presentation.mainActivity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.presentation.component.WearConfirmationOverlay
import com.xxmrk888ytxx.portal.presentation.component.WearConfirmationType
import com.xxmrk888ytxx.portal.presentation.deviceActions.DeviceActionsScreen
import com.xxmrk888ytxx.portal.presentation.deviceActions.DeviceActionsSideEffect
import com.xxmrk888ytxx.portal.presentation.deviceActions.DeviceActionsViewModel
import com.xxmrk888ytxx.portal.presentation.deviceList.DeviceListEvent
import com.xxmrk888ytxx.portal.presentation.deviceList.DeviceListScreen
import com.xxmrk888ytxx.portal.presentation.deviceList.DeviceListSideEffect
import com.xxmrk888ytxx.portal.presentation.deviceList.DeviceListViewModel
import com.xxmrk888ytxx.portal.presentation.permissionGate.PermissionGateScreen
import com.xxmrk888ytxx.portal.presentation.settings.SettingsScreen
import com.xxmrk888ytxx.portal.presentation.settings.SettingsSideEffect
import com.xxmrk888ytxx.portal.presentation.settings.SettingsViewModel
import com.xxmrk888ytxx.portal.presentation.theme.PortalTheme
import javax.inject.Inject

/**
 * Single Activity host for the main Wear OS application navigation.
 *
 * Incoming requests are hosted separately in [com.xxmrk888ytxx.portal.presentation.incomingRequest.IncomingRequestActivity].
 */
class MainActivity @Inject constructor(
    private val viewModelFactory: MainActivityViewModel.Factory,
    private val deviceListViewModelFactory: DeviceListViewModel.Factory,
    private val deviceActionsViewModelFactory: DeviceActionsViewModel.Factory,
    private val settingsViewModelFactory: SettingsViewModel.Factory
) : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel> { viewModelFactory }
    private val deviceListViewModel by viewModels<DeviceListViewModel> { deviceListViewModelFactory }
    private val deviceActionsViewModel by viewModels<DeviceActionsViewModel> { deviceActionsViewModelFactory }
    private val settingsViewModel by viewModels<SettingsViewModel> { settingsViewModelFactory }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        viewModel.handleEvent(MainActivityEvent.RefreshPermissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val devices by deviceListViewModel.devices.collectAsStateWithLifecycle()
            val isPhoneConnected by settingsViewModel.isPhoneConnected.collectAsStateWithLifecycle()
            val isActionLoading by deviceActionsViewModel.isLoading.collectAsStateWithLifecycle()
            val navController = rememberSwipeDismissableNavController()
            var confirmationData by remember { mutableStateOf<WearConfirmationData?>(null) }

            LaunchedEffect(Unit) {
                viewModel.sideEffect.collect { effect ->
                    when (effect) {
                        NavigationSideEffect.OpenNotificationSettings ->
                            openNotificationSettings()

                        is NavigationSideEffect.ShowMessage ->
                            confirmationData = WearConfirmationData(
                                type = WearConfirmationType.SUCCESS,
                                message = effect.message
                            )
                    }
                }
            }

            LaunchedEffect(Unit) {
                deviceListViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        DeviceListSideEffect.OpenSettings -> {
                            navController.navigate(WearRoutes.SETTINGS)
                        }

                        is DeviceListSideEffect.OpenDeviceActions -> {
                            viewModel.handleEvent(
                                MainActivityEvent.ShowDeviceActions(effect.device)
                            )
                            navController.navigate(WearRoutes.DEVICE_ACTIONS)
                        }

                        DeviceListSideEffect.ShowRefreshError -> {
                            confirmationData = WearConfirmationData(
                                type = WearConfirmationType.FAILURE,
                                message = getString(R.string.failed_to_refresh_devices)
                            )
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                deviceActionsViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        DeviceActionsSideEffect.NavigateBack -> {
                            navController.popBackStack()
                        }

                        DeviceActionsSideEffect.ShowCommandSent -> {
                            confirmationData = WearConfirmationData(
                                type = WearConfirmationType.SUCCESS,
                                message = getString(R.string.command_sent)
                            )
                        }

                        DeviceActionsSideEffect.ShowCommandError -> {
                            confirmationData = WearConfirmationData(
                                type = WearConfirmationType.FAILURE,
                                message = getString(R.string.failed_to_send_command)
                            )
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                settingsViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        SettingsSideEffect.NavigateBack -> {
                            navController.popBackStack()
                        }

                        SettingsSideEffect.OpenNotificationSettings -> {
                            viewModel.handleEvent(MainActivityEvent.OpenNotificationSettings)
                        }
                    }
                }
            }

            PortalTheme {
                AppScaffold(
                    timeText = { TimeText() }
                ) {
                    SwipeDismissableNavHost(
                        navController = navController,
                        startDestination = if (!state.permissions.canEnterApp) {
                            WearRoutes.PERMISSION_GATE
                        } else {
                            WearRoutes.DEVICE_LIST
                        }
                    ) {
                        composable(WearRoutes.PERMISSION_GATE) {
                            PermissionGateScreen(state, viewModel::handleEvent)
                        }

                        composable(WearRoutes.DEVICE_LIST) {
                            DeviceListScreen(
                                devices = devices,
                                onEvent = deviceListViewModel::handleEvent
                            )
                        }

                        composable(WearRoutes.DEVICE_ACTIONS) {
                            val selectedDevice = state.selectedDevice
                            if (selectedDevice != null) {
                                DeviceActionsScreen(
                                    device = selectedDevice,
                                    isLoading = isActionLoading,
                                    onEvent = deviceActionsViewModel::handleEvent
                                )
                            }
                        }

                        composable(WearRoutes.SETTINGS) {
                            SettingsScreen(
                                state = state,
                                isPhoneConnected = isPhoneConnected,
                                onEvent = settingsViewModel::handleEvent
                            )
                        }
                    }

                    confirmationData?.let { data ->
                        WearConfirmationOverlay(
                            visible = true,
                            message = data.message,
                            type = data.type,
                            onDismissRequest = { confirmationData = null }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleEvent(MainActivityEvent.RefreshPermissions)
        deviceListViewModel.handleEvent(DeviceListEvent.SilentRefreshDevices)
    }

    private fun openNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            runCatching {
                startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    }
                )
            }
        }
    }
}

data class WearConfirmationData(
    val type: WearConfirmationType,
    val message: String
)

object WearRoutes {
    const val PERMISSION_GATE = "permission_gate"
    const val DEVICE_LIST = "device_list"
    const val DEVICE_ACTIONS = "device_actions"
    const val SETTINGS = "settings"
}
