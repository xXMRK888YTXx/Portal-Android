package com.xxmrk888ytxx.portal.presentation.mainActivity

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.portal.data.WearDecisionPayloadValue
import com.xxmrk888ytxx.portal.domain.IncomingRequestRepository
import com.xxmrk888ytxx.portal.domain.WearPermissionChecker
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import com.xxmrk888ytxx.portal.domain.WearProfileRepository
import com.xxmrk888ytxx.portal.domain.WearSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class MainActivityViewModel @Inject constructor(
    private val context: Context,
    private val wearProfileRepository: WearProfileRepository,
    private val incomingRequestRepository: IncomingRequestRepository,
    private val wearSettingsRepository: WearSettingsRepository,
    private val permissionChecker: WearPermissionChecker,
    private val wearPhoneGateway: WearPhoneGateway
) : ViewModel() {

    private val transientState = MutableStateFlow(
        TransientState(
            permissions = permissionChecker.getState()
        )
    )

    val state: StateFlow<MainScreenState> = combine(
        wearProfileRepository.profiles,
        incomingRequestRepository.pendingRequest,
        wearSettingsRepository.showRequestsOnLockedScreen,
        transientState
    ) { profiles, request, showRequestsOnLockedScreen, transient ->
        val forcedShowOnLockedScreen = transient.permissions.canDrawOverlays &&
                !transient.permissions.canPostNotifications

        MainScreenState(
            profiles = profiles,
            selectedProfile = transient.selectedProfile?.let { selected ->
                profiles.firstOrNull { it.clientId == selected.clientId }
            },
            incomingRequest = request,
            permissions = transient.permissions,
            showRequestsOnLockedScreen = if (forcedShowOnLockedScreen) true else showRequestsOnLockedScreen,
            screen = transient.screen,
            message = transient.message
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MainScreenState()
    )

    fun handleEvent(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.OnResume -> refreshPermissions()
            is MainScreenEvent.SelectProfile -> transientState.update {
                it.copy(selectedProfile = event.profile)
            }

            MainScreenEvent.BackToMain -> transientState.update {
                it.copy(screen = WearScreen.Main, selectedProfile = null)
            }

            MainScreenEvent.OpenSettings -> transientState.update {
                it.copy(screen = WearScreen.Settings)
            }

            MainScreenEvent.OpenNotificationSettings -> openNotificationSettings()
            MainScreenEvent.OpenOverlaySettings -> openOverlaySettings()
            is MainScreenEvent.SetShowRequestsOnLockedScreen -> setShowRequestsOnLockedScreen(event.value)
            is MainScreenEvent.Unlock -> sendCommand { wearPhoneGateway.sendUnlockCommand(event.clientId) }
            is MainScreenEvent.WakeOnLanUnlock -> sendCommand {
                wearPhoneGateway.sendWakeOnLanUnlockCommand(event.clientId)
            }

            is MainScreenEvent.OpenIncomingRequest -> transientState.update {
                it.copy(screen = WearScreen.IncomingRequest)
            }

            MainScreenEvent.AllowIncomingRequest -> resolveIncomingRequest(WearDecisionPayloadValue.UNLOCK)
            MainScreenEvent.CancelIncomingRequest -> resolveIncomingRequest(WearDecisionPayloadValue.CANCEL)
            MainScreenEvent.ClearMessage -> transientState.update { it.copy(message = null) }
        }
    }

    private fun refreshPermissions() {
        val permissions = permissionChecker.getState()
        transientState.update { it.copy(permissions = permissions) }

        if (permissions.canDrawOverlays && !permissions.canPostNotifications) {
            wearSettingsRepository.setShowRequestsOnLockedScreen(true)
        }
    }

    private fun setShowRequestsOnLockedScreen(value: Boolean) {
        val permissions = permissionChecker.getState()
        if (!value && permissions.canDrawOverlays && !permissions.canPostNotifications) {
            wearSettingsRepository.setShowRequestsOnLockedScreen(true)
            transientState.update {
                it.copy(message = "Notification permission is required to disable this option")
            }
            return
        }
        wearSettingsRepository.setShowRequestsOnLockedScreen(value)
    }

    private fun sendCommand(block: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { block() }
                .onSuccess {
                    transientState.update { it.copy(message = "Command sent") }
                }
                .onFailure { throwable ->
                    transientState.update {
                        it.copy(message = throwable.message ?: "Failed to send command")
                    }
                }
        }
    }

    private fun resolveIncomingRequest(decision: WearDecisionPayloadValue) {
        val request = state.value.incomingRequest ?: return
        viewModelScope.launch {
            runCatching { wearPhoneGateway.sendDecision(request.decisionId, decision) }
                .onSuccess {
                    incomingRequestRepository.clear(request.decisionId)
                    transientState.update { it.copy(screen = WearScreen.Main) }
                }
                .onFailure { throwable ->
                    transientState.update {
                        it.copy(message = throwable.message ?: "Failed to send decision")
                    }
                }
        }
    }

    private fun openNotificationSettings() {
        context.startActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    private fun openOverlaySettings() {
        context.startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${context.packageName}".toUri()
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    private data class TransientState(
        val selectedProfile: com.xxmrk888ytxx.portal.domain.model.WearProfile? = null,
        val screen: WearScreen = WearScreen.Main,
        val permissions: com.xxmrk888ytxx.portal.domain.WearPermissionState,
        val message: String? = null
    )

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<MainActivityViewModel>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}
