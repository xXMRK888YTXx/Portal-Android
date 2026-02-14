package com.xxmrk888ytxx.portal.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xxmrk888ytxx.addnewdevicescreen.AddNewDeviceScreen
import com.xxmrk888ytxx.addnewdevicescreen.AddNewDeviceViewModel
import com.xxmrk888ytxx.coreandroid.PortalViewModel
import com.xxmrk888ytxx.corecompose.theme.setContentWithThemeAndProviders
import com.xxmrk888ytxx.goals.extensions.ScreenContent
import com.xxmrk888ytxx.goals.extensions.appComponent
import com.xxmrk888ytxx.mainscreen.MainScreen
import com.xxmrk888ytxx.mainscreen.MainScreenViewModel
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.MainScreenSideEffect
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import com.xxmrk888ytxx.onboardingscreen.OnboardingScreen
import com.xxmrk888ytxx.onboardingscreen.OnboardingViewModel
import com.xxmrk888ytxx.portal.domain.PreferenceManager
import com.xxmrk888ytxx.portal.view.model.Screen
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var activityViewModelFactory: ActivityViewModel.Factory
    private val activityViewModel by viewModels<ActivityViewModel> { activityViewModelFactory }

    //Screen viewModels
    @Inject
    lateinit var onboardingViewModelFactory: Provider<OnboardingViewModel>

    @Inject
    lateinit var mainScreenViewModelFactory: Provider<MainScreenViewModel>

    @Inject
    lateinit var addNewDeviceViewModelFactory: Provider<AddNewDeviceViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        activityViewModel.prepareScreen()
        enableEdgeToEdge()
        installSplashScreen().setKeepOnScreenCondition { !activityViewModel.isScreenReady.value }
        setContentWithThemeAndProviders(
            navigator = activityViewModel
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
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
