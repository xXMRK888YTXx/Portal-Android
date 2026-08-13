package com.xxmrk888ytxx.portal.presentation.mainActivity

import com.xxmrk888ytxx.portal.domain.WearPermissionState
import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest
import com.xxmrk888ytxx.portal.domain.model.WearProfile

data class MainScreenState(
    val profiles: List<WearProfile> = emptyList(),
    val selectedProfile: WearProfile? = null,
    val incomingRequest: IncomingUnlockRequest? = null,
    val permissions: WearPermissionState = WearPermissionState(
        canPostNotifications = false,
        canDrawOverlays = false
    ),
    val showRequestsOnLockedScreen: Boolean = false,
    val screen: WearScreen = WearScreen.Main,
    val message: String? = null
)

enum class WearScreen {
    Main,
    Settings,
    IncomingRequest
}
