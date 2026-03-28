package com.xxmrk888ytxx.portal.view.unlockScreenActivity

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.mvi.SideEffectSender
import com.xxmrk888ytxx.portal.domain.BiometricDialogController
import com.xxmrk888ytxx.portal.domain.ProvideDeviceNameByClientId
import com.xxmrk888ytxx.portal.domain.UnlockMessageSender
import com.xxmrk888ytxx.portal.domain.UnlockServiceManager
import com.xxmrk888ytxx.portal.domain.model.BiometricDialogEvent
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import com.xxmrk888ytxx.portal.utils.getParsableExtraCompat
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.model.UnlockScreenData
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.model.UnlockScreenSideEffect
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class UnlockScreenViewModel @Inject constructor(
    private val biometricDialogController: BiometricDialogController,
    private val unlockMessageSender: UnlockMessageSender,
    private val provideDeviceNameByClientId: ProvideDeviceNameByClientId
) : ViewModel(), Navigator, SideEffectSender<UnlockScreenSideEffect> {

    private val _effect = MutableSharedFlow<UnlockScreenSideEffect>(extraBufferCapacity = 1)

    override val effect: Flow<UnlockScreenSideEffect> = _effect.asSharedFlow()

    private var unlockScreenData: UnlockScreenData? = null

    private val isEventSent = MutableStateFlow(false)


    fun requestBiometricAuth(activity: FragmentActivity) = viewModelScope.launch {
        val deviceName = provideDeviceNameByClientId.provideName(unlockScreenData?.clientId ?: return@launch)
        biometricDialogController.sendRequest(
            activity = activity,
            onEvent = {
                when (it) {
                    BiometricDialogEvent.Success -> unlockScreenData?.let { unlockData -> unlockHost(unlockData) }

                    BiometricDialogEvent.Canceled, BiometricDialogEvent.Error -> sendCancelEventAndDismissScreen()

                    BiometricDialogEvent.Failed -> Unit
                }
            },
            description = deviceName
        )
    }

    private fun unlockHost(unlockScreenData: UnlockScreenData) {
        if (isEventSent.value) return
        isEventSent.value = true
        viewModelScope.launch {
            unlockMessageSender.sendMessage(
                unlockScreenData.clientId,
                UnlockServiceMessage.Unlock(requestId = unlockScreenData.requestId)
            )
        }.invokeOnCompletion { dismissScreen() }
    }

    private fun dismissScreen() {
        _effect.tryEmit(UnlockScreenSideEffect.Dismiss)
    }


    override fun fromOnboardingScreenToMainScreen() {

    }

    override fun fromMainScreenToAddNewDeviceScreen() {
    }

    override fun fromMainScreenToDeviceConfigurationScreen(deviceId: String) {
    }

    override fun fromAddNewDeviceScreenToDeviceConfigurationScreen(deviceId: String) {

    }

    override fun fromSettingsScreenToLogsScreen() {
        
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

    @Suppress("CoroutineContextWithJob")
    private fun sendCancelEventAndDismissScreen() {
        if (isEventSent.value) return
        isEventSent.value = true
        viewModelScope.launch(NonCancellable) {
            unlockScreenData?.let { unlockData -> unlockMessageSender.sendMessage(unlockData.clientId, UnlockServiceMessage.Canceled(unlockData.requestId)) }
        }.invokeOnCompletion { dismissScreen() }
    }

    fun onStop() = sendCancelEventAndDismissScreen()

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModel: Provider<UnlockScreenViewModel>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModel.get() as T
        }
    }
}