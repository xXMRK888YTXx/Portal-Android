package com.xxmrk888ytxx.portal.view.unlockScreenActivity

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.mvi.SideEffectSender
import com.xxmrk888ytxx.portal.domain.BiometricDialogController
import com.xxmrk888ytxx.portal.domain.UnlockServiceManager
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import com.xxmrk888ytxx.portal.utils.getParsableExtraCompat
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.model.UnlockScreenData
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.model.UnlockScreenSideEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class UnlockScreenViewModel @Inject constructor(
    private val biometricDialogController: BiometricDialogController,
    private val unlockServiceManager: UnlockServiceManager
) : ViewModel(), Navigator, SideEffectSender<UnlockScreenSideEffect> {

    private val _effect = MutableSharedFlow<UnlockScreenSideEffect>(extraBufferCapacity = 1)

    override val effect: Flow<UnlockScreenSideEffect> = _effect.asSharedFlow()

    private var unlockScreenData: UnlockScreenData? = null


    fun requestBiometricAuth(activity: FragmentActivity) = viewModelScope.launch {
        biometricDialogController.sendRequest(
            activity = activity,
            onSuccess = {
                viewModelScope.launch {
                    unlockScreenData?.let {
                        unlockServiceManager.sendMessageToHost(
                            it.clientId,
                            UnlockServiceMessage.Unlock
                        )
                    }
                    _effect.tryEmit(UnlockScreenSideEffect.Dismiss)
                }
            },
            onFailed = {
                _effect.tryEmit(UnlockScreenSideEffect.Dismiss)
            }
        )
    }

    override fun fromOnboardingScreenToMainScreen() {

    }

    override fun fromMainScreenToAddNewDeviceScreen() {
    }

    override fun fromMainScreenToDeviceConfigurationScreen(deviceId: String) {
    }

    override fun navigateUp() {
    }

    fun isValidIntent(intent: Intent?): Boolean {
        unlockScreenData = intent?.getParsableExtraCompat(
            UnlockScreenActivity.EXTRA_UNLOCK_SCREEN_DATA,
            UnlockScreenData::class.java
        ) ?: return false
        return true
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModel: Provider<UnlockScreenViewModel>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModel.get() as T
        }
    }
}