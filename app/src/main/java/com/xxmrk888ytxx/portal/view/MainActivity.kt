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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xxmrk888ytxx.corecompose.theme.setContentWithThemeAndProviders
import com.xxmrk888ytxx.goals.extensions.appComponent
import com.xxmrk888ytxx.onboardingscreen.OnboardingScreen
import com.xxmrk888ytxx.onboardingscreen.OnboardingViewModel
import com.xxmrk888ytxx.portal.view.model.Screen
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var activityViewModelFactory: ActivityViewModel.Factory
    private val activityViewModel by viewModels<ActivityViewModel> { activityViewModelFactory }

    //Screen viewModels
    @Inject
    lateinit var  onboardingViewModelFactory: Provider<OnboardingViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        enableEdgeToEdge()
        setContentWithThemeAndProviders(
            navigator = activityViewModel
        ) {
            val backStack = rememberNavBackStack(Screen.OnboardingScreen)

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
                            val viewModel = viewModel<OnboardingViewModel> { onboardingViewModelFactory.get() }
                            val state by viewModel.state.collectAsState()
                            OnboardingScreen(state,viewModel::handleEvent,viewModel.effect)
                        }

                        entry<Screen.MainScreen> {
                            Text("MainScreen")
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
