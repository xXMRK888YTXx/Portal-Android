package com.xxmrk888ytxx.portal.presentation.mainActivity

import com.xxmrk888ytxx.portal.domain.model.WearProfile

sealed interface MainScreenEvent {
    data object OnResume : MainScreenEvent
    data class SelectProfile(val profile: WearProfile) : MainScreenEvent
    data object BackToMain : MainScreenEvent
    data object OpenSettings : MainScreenEvent
    data object OpenNotificationSettings : MainScreenEvent
    data object OpenOverlaySettings : MainScreenEvent
    data class SetShowRequestsOnLockedScreen(val value: Boolean) : MainScreenEvent
    data class Unlock(val clientId: String) : MainScreenEvent
    data class WakeOnLanUnlock(val clientId: String) : MainScreenEvent
    data class OpenIncomingRequest(val decisionId: String) : MainScreenEvent
    data object AllowIncomingRequest : MainScreenEvent
    data object CancelIncomingRequest : MainScreenEvent
    data object ClearMessage : MainScreenEvent
}
