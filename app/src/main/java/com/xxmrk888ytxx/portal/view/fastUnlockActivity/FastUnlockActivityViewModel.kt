package com.xxmrk888ytxx.portal.view.fastUnlockActivity

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.portal.domain.BiometricDialogController
import com.xxmrk888ytxx.portal.domain.ShortcutRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class FastUnlockActivityViewModel @Inject constructor(
    private val shortcutRepository: ShortcutRepository,
    private val biometricDialogController: BiometricDialogController
) : ViewModel(), Navigator {

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

    fun requestUnlock(activity: FastUnlockActivity, intent: Intent) = viewModelScope.launch {
        try {
            val shortcutId =
                intent.getStringExtra(FastUnlockActivity.SHORTCUT_ID_EXTRA)
                    ?: throw IllegalArgumentException("Shortcut can't be null")
            val shortcut = shortcutRepository.getShortcutById(shortcutId)
                ?: throw IllegalArgumentException("Shortcut didn't registered")

            when {
                shortcut.isRequiredBiometricUnlock -> biometricDialogController.sendRequest(
                    activity,
                    onSuccess = {
                        startUnlockService(
                            activity.applicationContext,
                            shortcut.shortcutId
                        )
                    },
                    onFailed = { _onFinishEvent.tryEmit(Unit) }
                )

                else -> startUnlockService(
                    activity.applicationContext,
                    shortcut.shortcutId
                )
            }
        } catch (e: IllegalArgumentException) {
            _onFinishEvent.emit(Unit)
        }
    }

    private fun startUnlockService(context: Context, shortcutId: String) {
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