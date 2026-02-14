package com.xxmrk888ytxx.portal.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.runOnUiThread
import com.xxmrk888ytxx.portal.domain.PreferenceManager
import com.xxmrk888ytxx.portal.view.model.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

class ActivityViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
) : ViewModel(), Navigator {

    private val prepareScreenScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var backStack: NavBackStack<NavKey>? = null

    private val _startScreen = MutableStateFlow<Screen>(Screen.OnboardingScreen)
    val startScreen = _startScreen.asStateFlow()

    private val _isScreenReady = MutableStateFlow(false)
    val isScreenReady = _isScreenReady
        .asStateFlow()


    fun prepareScreen() {
        if (_isScreenReady.value) return
        prepareScreenScope.launch {
            preferenceManager.isOnboardingPassed.collect { isPassed ->
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

    override fun navigateUp() = runOnUiThread {
        backStack?.removeLastOrNull()
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