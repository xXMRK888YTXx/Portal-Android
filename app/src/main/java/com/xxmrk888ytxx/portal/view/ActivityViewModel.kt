package com.xxmrk888ytxx.portal.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.runOnUiThread
import com.xxmrk888ytxx.portal.view.model.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

class ActivityViewModel @Inject constructor() : ViewModel(), Navigator {

    var backStack: NavBackStack<NavKey>? = null

    override fun fromOnboardingScreenToMainScreen() = runOnUiThread {
        backStack?.add(Screen.MainScreen)
        backStack?.remove(Screen.OnboardingScreen)
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