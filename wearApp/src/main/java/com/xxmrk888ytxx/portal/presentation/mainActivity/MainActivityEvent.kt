package com.xxmrk888ytxx.portal.presentation.mainActivity

import com.xxmrk888ytxx.portal.domain.model.Device

/**
 * Host-level UI events for the Wear OS single-activity shell.
 *
 * Screen Composables send these events instead of invoking navigation or permission actions
 * directly.
 */
sealed interface MainActivityEvent {
    data object RefreshPermissions : MainActivityEvent
    data object ShowDevices : MainActivityEvent
    data class ShowDeviceActions(val device: Device) : MainActivityEvent
    data object ShowSettings : MainActivityEvent
    data object ShowIncomingRequest : MainActivityEvent
    data object OpenNotificationSettings : MainActivityEvent
    data class ShowMessage(val message: String) : MainActivityEvent
}
