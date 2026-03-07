package com.xxmrk888ytxx.portal.view.mainActivity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xxmrk888ytxx.addnewdevicescreen.AddNewDeviceScreen
import com.xxmrk888ytxx.addnewdevicescreen.AddNewDeviceViewModel
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.corecompose.theme.setContentWithThemeAndProviders
import com.xxmrk888ytxx.deviceconfigurationscreen.DeviceConfigurationScreen
import com.xxmrk888ytxx.deviceconfigurationscreen.DeviceConfigurationViewModel
import com.xxmrk888ytxx.portal.utils.ScreenContent
import com.xxmrk888ytxx.mainscreen.MainScreen
import com.xxmrk888ytxx.mainscreen.MainScreenViewModel
import com.xxmrk888ytxx.onboardingscreen.OnboardingScreen
import com.xxmrk888ytxx.onboardingscreen.OnboardingViewModel
import com.xxmrk888ytxx.portal.domain.BiometricActivityResultReceiver
import com.xxmrk888ytxx.portal.domain.BiometricDialogController
import com.xxmrk888ytxx.portal.utils.collectBiometricAuthResult
import com.xxmrk888ytxx.portal.view.model.Screen
import javax.inject.Inject
import javax.inject.Provider

class MainActivity @Inject constructor(
    private val activityViewModelFactory: ActivityViewModel.Factory,
    //Screen viewModels
    private val onboardingViewModelFactory: Provider<OnboardingViewModel>,
    private val mainScreenViewModelFactory: Provider<MainScreenViewModel>,
    private val addNewDeviceViewModelFactory: Provider<AddNewDeviceViewModel>,
    private val toastManager: ToastManager,
    private val deviceConfigurationViewModelFactory: DeviceConfigurationViewModel.Factory,
    private val biometricActivityResultReceiver: BiometricActivityResultReceiver,
    private val biometricDialogController: BiometricDialogController
) : FragmentActivity() {
    private val activityViewModel by viewModels<ActivityViewModel> { activityViewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityViewModel.prepareScreen()
        collectBiometricAuthResult(biometricActivityResultReceiver, biometricDialogController)
        enableEdgeToEdge()
        installSplashScreen().setKeepOnScreenCondition { !activityViewModel.isScreenReady.value }
        setContentWithThemeAndProviders(
            navigator = activityViewModel,
            toastManager = toastManager
        ) {
            val startScreen by activityViewModel.startScreen.collectAsState()

            val backStack = rememberNavBackStack(startScreen)

            LaunchedEffect(backStack) {
                activityViewModel.backStack = backStack
            }

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                NavDisplay(
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    backStack = backStack,
                    entryProvider = entryProvider {
                        entry<Screen.OnboardingScreen> {
                            ScreenContent(::OnboardingScreen, onboardingViewModelFactory)
                        }

                        entry<Screen.MainScreen> {
                            ScreenContent(::MainScreen, mainScreenViewModelFactory)
                        }

                        entry<Screen.AddNewDeviceScreen> {
                            ScreenContent(::AddNewDeviceScreen, addNewDeviceViewModelFactory)
                        }

                        entry<Screen.DeviceConfigurationScreen> { screen ->
                            ScreenContent(
                                ::DeviceConfigurationScreen,
                                { deviceConfigurationViewModelFactory.create(screen.deviceId) })
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
