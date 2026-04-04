package com.xxmrk888ytxx.portal.view.fastUnlockActivity

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.data.service.UnlockFromShortcutService
import com.xxmrk888ytxx.portal.domain.BiometricDialogController
import com.xxmrk888ytxx.portal.domain.ProvideDeviceNameByClientId
import com.xxmrk888ytxx.portal.domain.ShortcutRepository
import com.xxmrk888ytxx.portal.domain.model.BiometricDialogEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class FastUnlockActivityViewModel @Inject constructor(
    private val shortcutRepository: ShortcutRepository,
    private val biometricDialogController: BiometricDialogController,
    private val toastManager: ToastManager,
    private val provideDeviceNameByClientId: ProvideDeviceNameByClientId
) : ViewModel(), Navigator {

    private val _onFinishEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onFinishEvent = _onFinishEvent.asSharedFlow()

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

    fun requestUnlock(activity: ShortcutUnlockActivity, intent: Intent) = viewModelScope.launch {
        if (intent.action != ShortcutUnlockActivity.UNLOCK_FROM_SHORTCUT_ACTION) return@launch
        try {
            val shortcutId =
                intent.getStringExtra(ShortcutUnlockActivity.SHORTCUT_ID_EXTRA)
                    ?: throw IllegalArgumentException("Shortcut can't be null")
            val shortcut = shortcutRepository.getShortcutById(shortcutId)
                ?: let {
                    toastManager.showToast(R.string.the_device_associated_with_the_shortcut_cannot_be_found)
                    throw IllegalArgumentException("Shortcut didn't registered")
                }

            when {
                shortcut.isRequiredBiometricUnlock -> {
                    val deviceName = provideDeviceNameByClientId.provideName(shortcut.clientId)
                    biometricDialogController.sendRequest(
                        activity,
                        onEvent = {
                            when (it) {
                                BiometricDialogEvent.Success -> startUnlockService(
                                    activity.applicationContext,
                                    shortcut.clientId
                                )

                                BiometricDialogEvent.Canceled, BiometricDialogEvent.Error -> {
                                    _onFinishEvent.tryEmit(Unit)
                                }

                                BiometricDialogEvent.Failed -> {}
                            }
                        },
                        description = deviceName
                    )
                }

                else -> startUnlockService(
                    activity.applicationContext,
                    shortcut.clientId
                )
            }
        } catch (_: IllegalArgumentException) {
            _onFinishEvent.emit(Unit)
        }
    }

    private fun startUnlockService(context: Context, clientId: String) {
        val intent = Intent(context, UnlockFromShortcutService::class.java).apply {
            putExtra(UnlockFromShortcutService.DEVICE_ID_EXTRA, clientId)
            action = UnlockFromShortcutService.SHORTCUT_UNLOCK_ACTION
        }
        context.startForegroundService(intent)
        _onFinishEvent.tryEmit(Unit)
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