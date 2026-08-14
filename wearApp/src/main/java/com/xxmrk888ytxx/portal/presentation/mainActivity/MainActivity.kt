package com.xxmrk888ytxx.portal.presentation.mainActivity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.AppScaffold
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.model.Device
import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest
import com.xxmrk888ytxx.portal.presentation.deviceActions.DeviceActionsScreen
import com.xxmrk888ytxx.portal.presentation.deviceActions.DeviceActionsSideEffect
import com.xxmrk888ytxx.portal.presentation.deviceActions.DeviceActionsViewModel
import com.xxmrk888ytxx.portal.presentation.deviceList.DeviceListEvent
import com.xxmrk888ytxx.portal.presentation.deviceList.DeviceListScreen
import com.xxmrk888ytxx.portal.presentation.deviceList.DeviceListSideEffect
import com.xxmrk888ytxx.portal.presentation.deviceList.DeviceListViewModel
import com.xxmrk888ytxx.portal.presentation.incomingRequest.IncomingRequestScreen
import com.xxmrk888ytxx.portal.presentation.incomingRequest.IncomingRequestSideEffect
import com.xxmrk888ytxx.portal.presentation.incomingRequest.IncomingRequestViewModel
import com.xxmrk888ytxx.portal.presentation.permissionGate.PermissionGateScreen
import com.xxmrk888ytxx.portal.presentation.settings.SettingsScreen
import com.xxmrk888ytxx.portal.presentation.settings.SettingsSideEffect
import com.xxmrk888ytxx.portal.presentation.settings.SettingsViewModel
import com.xxmrk888ytxx.portal.presentation.theme.PortalTheme
import javax.inject.Inject

/**
 * Single Activity host for the Wear OS app.
 *
 * The activity wires ViewModel state and side effects to Composables. It intentionally keeps
 * Android-only actions, such as launching notification permission UI, outside screen Composables.
 */
class MainActivity @Inject constructor(
    private val viewModelFactory: MainActivityViewModel.Factory,
    private val deviceListViewModelFactory: DeviceListViewModel.Factory,
    private val deviceActionsViewModelFactory: DeviceActionsViewModel.Factory,
    private val settingsViewModelFactory: SettingsViewModel.Factory,
    private val incomingRequestViewModelFactory: IncomingRequestViewModel.Factory
) : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel> { viewModelFactory }
    private val deviceListViewModel by viewModels<DeviceListViewModel> { deviceListViewModelFactory }
    private val deviceActionsViewModel by viewModels<DeviceActionsViewModel> { deviceActionsViewModelFactory }
    private val settingsViewModel by viewModels<SettingsViewModel> { settingsViewModelFactory }
    private val incomingRequestViewModel by viewModels<IncomingRequestViewModel> {
        incomingRequestViewModelFactory
    }
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        viewModel.handleEvent(MainActivityEvent.RefreshPermissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == ACTION_OPEN_REQUEST) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
                setTurnScreenOn(true)
            }
            viewModel.handleEvent(MainActivityEvent.ShowIncomingRequest)
        }

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val devices by deviceListViewModel.devices.collectAsStateWithLifecycle()
            val isPhoneConnected by settingsViewModel.isPhoneConnected.collectAsStateWithLifecycle()
            val incomingRequest by incomingRequestViewModel.request.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.sideEffect.collect { effect ->
                    when (effect) {
                        NavigationSideEffect.OpenNotificationSettings ->
                            openNotificationSettings()

                        is NavigationSideEffect.ShowMessage ->
                            Toast.makeText(this@MainActivity, effect.message, Toast.LENGTH_SHORT)
                                .show()
                    }
                }
            }

            LaunchedEffect(Unit) {
                deviceListViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        DeviceListSideEffect.OpenSettings -> {
                            viewModel.handleEvent(MainActivityEvent.ShowSettings)
                        }

                        is DeviceListSideEffect.OpenDeviceActions -> {
                            viewModel.handleEvent(
                                MainActivityEvent.ShowDeviceActions(effect.device)
                            )
                        }

                        DeviceListSideEffect.ShowRefreshError -> {
                            viewModel.handleEvent(
                                MainActivityEvent.ShowMessage(
                                    getString(R.string.failed_to_refresh_devices)
                                )
                            )
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                deviceActionsViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        DeviceActionsSideEffect.NavigateBack -> {
                            viewModel.handleEvent(MainActivityEvent.ShowDevices)
                        }

                        DeviceActionsSideEffect.ShowCommandSent -> {
                            viewModel.handleEvent(
                                MainActivityEvent.ShowMessage(getString(R.string.command_sent))
                            )
                        }

                        DeviceActionsSideEffect.ShowCommandError -> {
                            viewModel.handleEvent(
                                MainActivityEvent.ShowMessage(
                                    getString(R.string.failed_to_send_command)
                                )
                            )
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                settingsViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        SettingsSideEffect.NavigateBack -> {
                            viewModel.handleEvent(MainActivityEvent.ShowDevices)
                        }

                        SettingsSideEffect.OpenNotificationSettings -> {
                            viewModel.handleEvent(MainActivityEvent.OpenNotificationSettings)
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                incomingRequestViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        IncomingRequestSideEffect.NavigateBack -> {
                            viewModel.handleEvent(MainActivityEvent.ShowDevices)
                        }

                        IncomingRequestSideEffect.ShowDecisionError -> {
                            viewModel.handleEvent(
                                MainActivityEvent.ShowMessage(
                                    getString(R.string.failed_to_send_decision)
                                )
                            )
                        }
                    }
                }
            }

            PortalTheme {
                WearApp(
                    state = state,
                    devices = devices,
                    isPhoneConnected = isPhoneConnected,
                    incomingRequest = incomingRequest,
                    deviceListViewModel = deviceListViewModel,
                    deviceActionsViewModel = deviceActionsViewModel,
                    settingsViewModel = settingsViewModel,
                    incomingRequestViewModel = incomingRequestViewModel,
                    onMainEvent = viewModel::handleEvent
                )
            }
        }
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

    companion object {
        const val ACTION_OPEN_REQUEST = "com.xxmrk888ytxx.portal.wear.OPEN_REQUEST"
        const val EXTRA_DECISION_ID = "decisionId"
    }
}

@androidx.compose.runtime.Composable
private fun WearApp(
    state: MainScreenState,
    devices: List<Device>,
    isPhoneConnected: Boolean?,
    incomingRequest: IncomingUnlockRequest?,
    deviceListViewModel: DeviceListViewModel,
    deviceActionsViewModel: DeviceActionsViewModel,
    settingsViewModel: SettingsViewModel,
    incomingRequestViewModel: IncomingRequestViewModel,
    onMainEvent: (MainActivityEvent) -> Unit
) {
    AppScaffold {
        when {
            !state.permissions.canEnterApp -> PermissionGateScreen(state, onMainEvent)
            state.screen == WearScreen.Settings -> SettingsScreen(
                state = state,
                isPhoneConnected = isPhoneConnected,
                onEvent = settingsViewModel::handleEvent
            )

            state.screen == WearScreen.IncomingRequest -> IncomingRequestScreen(
                request = incomingRequest,
                onEvent = incomingRequestViewModel::handleEvent
            )

            state.screen == WearScreen.DeviceActions && state.selectedDevice != null ->
                DeviceActionsScreen(
                    device = state.selectedDevice,
                    onEvent = deviceActionsViewModel::handleEvent
                )

            else -> DeviceListScreen(
                devices = devices,
                onEvent = deviceListViewModel::handleEvent
            )
        }
    }
}
