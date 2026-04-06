package com.xxmrk888ytxx.portal.view.unlockScreenActivity

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.PortalViewModel
import com.xxmrk888ytxx.coreandroid.mvi.SideEffectSender
import com.xxmrk888ytxx.coreandroid.mvi.UiEvent
import com.xxmrk888ytxx.coreandroid.mvi.UiEventHandler
import com.xxmrk888ytxx.portal.domain.BiometricDialogController
import com.xxmrk888ytxx.portal.domain.ProvideDeviceNameByClientId
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.portal.domain.UnlockMessageSender
import com.xxmrk888ytxx.portal.domain.model.BiometricDialogEvent
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import com.xxmrk888ytxx.portal.utils.getParsableExtraCompat
import com.xxmrk888ytxx.portal.view.model.UnlockScreenUiEvent
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.model.UnlockScreenData
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.model.UnlockScreenSideEffect
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class UnlockScreenViewModel @Inject constructor(
    private val biometricDialogController: BiometricDialogController,
    private val unlockMessageSender: UnlockMessageSender,
    private val provideDeviceNameByClientId: ProvideDeviceNameByClientId,
    private val settingsRepository: SettingsRepository,
) : PortalViewModel<Unit, UnlockScreenUiEvent>(Unit), Navigator, SideEffectSender<UnlockScreenSideEffect> {

    private val _effect = MutableSharedFlow<UnlockScreenSideEffect>(extraBufferCapacity = 1, replay = 1)

    override val effect: Flow<UnlockScreenSideEffect> = _effect.asSharedFlow()

    private var unlockScreenData: UnlockScreenData? = null

    private val isEventSent = MutableStateFlow(false)
    private val isAllowEventHandling = MutableStateFlow(false)

    private val _deviceName = MutableStateFlow("")
    val deviceName: StateFlow<String> = _deviceName.asStateFlow()

    val themeColor = settingsRepository.portalSettings.map { it.themeColor }.stateWhileSubscribed(null)

    private fun requestBiometricAuth(activity: FragmentActivity) = viewModelScope.launch {
        biometricDialogController.sendRequest(
            activity = activity,
            onEvent = {
                when (it) {
                    BiometricDialogEvent.Success -> unlockScreenData?.let { unlockData ->
                        unlockHost(
                            unlockData
                        )
                    }

                    BiometricDialogEvent.Error -> sendCancelEventAndDismissScreen()

                    BiometricDialogEvent.Failed, BiometricDialogEvent.Canceled -> Unit
                }
            },
            description = _deviceName.value
        )
    }

    override fun handleEvent(event: UnlockScreenUiEvent) {
        when (event) {
            is UnlockScreenUiEvent.Allow -> allowUnlock(event)
            UnlockScreenUiEvent.Deny -> sendCancelEventAndDismissScreen()
        }
    }

    private fun allowUnlock(event: UnlockScreenUiEvent.Allow) {
        if (isAllowEventHandling.value) return
        isAllowEventHandling.value = true
        viewModelScope.launch {
            val isBiometricUnlockEnabled = settingsRepository.portalSettings.first().isBiometricAuthEnabled
            if (isBiometricUnlockEnabled) {
                requestBiometricAuth(event.fragmentActivity)
            } else {
                if (event.isSentByUser)
                    unlockScreenData?.let { unlockHost(it) }
            }
        }.invokeOnCompletion { isAllowEventHandling.value = false }
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
        if (unlockScreenData != null) return true
        if (intent?.action != UnlockScreenActivity.UNLOCK_REQUEST_FROM_PC_ACTION) return false
        val intentUnlockScreenData = intent.getParsableExtraCompat(
            UnlockScreenActivity.EXTRA_UNLOCK_SCREEN_DATA,
            UnlockScreenData::class.java
        ) ?: return false
        unlockScreenData = intentUnlockScreenData
        viewModelScope.launch {
            _deviceName.value =
                provideDeviceNameByClientId.provideName(intentUnlockScreenData.clientId) ?: ""
        }
        return true
    }

    @Suppress("CoroutineContextWithJob")
    private fun sendCancelEventAndDismissScreen() {
        if (isEventSent.value) return
        isEventSent.value = true
        viewModelScope.launch(NonCancellable) {
            unlockScreenData?.let { unlockData ->
                unlockMessageSender.sendMessage(
                    unlockData.clientId,
                    UnlockServiceMessage.Canceled(unlockData.requestId)
                )
            }
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