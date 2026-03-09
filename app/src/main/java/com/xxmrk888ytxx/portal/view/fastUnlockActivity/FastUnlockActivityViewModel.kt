package com.xxmrk888ytxx.portal.view.fastUnlockActivity

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xxmrk888ytxx.coreandroid.Navigator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Provider

class FastUnlockActivityViewModel @Inject constructor() : ViewModel(), Navigator {

    private val _onFinishEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onFinishEvent = _onFinishEvent.asSharedFlow()

    override fun fromOnboardingScreenToMainScreen() {

    }

    override fun fromMainScreenToAddNewDeviceScreen() {

    }

    override fun fromMainScreenToDeviceConfigurationScreen(deviceId: String) {

    }

    override fun navigateUp() {

    }

    fun requestUnlock(activity: FastUnlockActivity, intent: Intent) {

    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModel: Provider<FastUnlockActivityViewModel>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModel.get() as T
        }
    }
}