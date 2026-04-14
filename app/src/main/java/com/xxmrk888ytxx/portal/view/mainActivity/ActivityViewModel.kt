package com.xxmrk888ytxx.portal.view.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.mvi.UiEvent
import com.xxmrk888ytxx.coreandroid.runOnUiThread
import com.xxmrk888ytxx.portal.domain.BiometricAuthStateProvider
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.portal.view.mainActivity.model.MainActivitySideEffect
import com.xxmrk888ytxx.portal.view.model.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class ActivityViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val bluetoothManager: BluetoothManager,
    private val biometricAuthStateProvider: BiometricAuthStateProvider,
) : SideEffectPortalViewModel<Unit, UiEvent>(Unit), Navigator {

    private val prepareScreenScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var backStack: NavBackStack<NavKey>? = null

    private val _startScreen = MutableStateFlow<Screen>(Screen.OnboardingScreen)
    val startScreen = _startScreen.asStateFlow()

    private val _isScreenReady = MutableStateFlow(false)
    val isScreenReady = _isScreenReady
        .asStateFlow()

    val themeColor = settingsRepository.portalSettings.map { it.themeColor }.stateWhileSubscribed(null)


    fun prepareScreen() {
        if (_isScreenReady.value) return
        prepareScreenScope.launch {
            settingsRepository.portalSettings.map { it.isOnboardingPassed }.collect { isPassed ->
                if (isPassed) {
                    _startScreen.value = Screen.MainScreen
                }
                _isScreenReady.value = true
                prepareScreenScope.cancel()
            }
        }
    }


    override fun fromOnboardingScreenToMainScreen() = runOnUiThread {
        backStack?.add(Screen.MainScreen)
        backStack?.remove(Screen.OnboardingScreen)
    }

    override fun fromMainScreenToAddNewDeviceScreen() = runOnUiThread {
        backStack?.add(Screen.AddNewDeviceScreen)
    }

    override fun fromMainScreenToDeviceConfigurationScreen(deviceId: String) = runOnUiThread {
        backStack?.add(Screen.DeviceConfigurationScreen(deviceId))
    }

    override fun fromAddNewDeviceScreenToDeviceConfigurationScreen(deviceId: String) = runOnUiThread {
        backStack?.add(Screen.DeviceConfigurationScreen(deviceId))
        backStack?.remove(Screen.AddNewDeviceScreen)
    }

    override fun fromSettingsScreenToLogsScreen() = runOnUiThread {
        backStack?.add(Screen.LogsScreen)
    }

    override fun navigateUp() = runOnUiThread {
        if (backStack?.size == 1) {
            sideEffect.tryEmit(MainActivitySideEffect.FinishActivity)
        } else {
            backStack?.removeLastOrNull()
        }
    }

    fun onResume() = viewModelScope.launch {
        launch { bluetoothManager.updatePairedDeviceMacAddresses() }
        launch { biometricAuthStateProvider.updateState() }
    }

    override fun handleEvent(event: UiEvent) {}

    internal inner class BottomBarNavigation {
        fun toSettingsScreen() {
            if (backStack?.lastOrNull() == Screen.SettingsScreen) return
            backStack?.add(Screen.SettingsScreen)
        }

        fun toMainScreen() {
            fastDebugLog(backStack?.joinToString(":") { it.toString() })
            if (backStack?.lastOrNull() == Screen.MainScreen) return
            if (backStack?.contains(Screen.MainScreen) != true) {
                backStack?.add(Screen.MainScreen)
            }
            backStack?.remove(Screen.SettingsScreen)
        }

    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModel: Provider<ActivityViewModel>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModel.get() as T
        }
    }
}