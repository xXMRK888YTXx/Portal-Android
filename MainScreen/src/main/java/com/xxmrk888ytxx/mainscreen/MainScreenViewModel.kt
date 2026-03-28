package com.xxmrk888ytxx.mainscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import com.xxmrk888ytxx.mainscreen.contract.CreateShortcutContract
import com.xxmrk888ytxx.mainscreen.contract.PermissionContract
import com.xxmrk888ytxx.mainscreen.contract.ManageDevicesRemovedBannerStateContract
import com.xxmrk888ytxx.mainscreen.contract.ProvideSavedDevices
import com.xxmrk888ytxx.mainscreen.contract.SendUnlockRequestContract
import com.xxmrk888ytxx.mainscreen.exception.LauncherNotSupportShortcutException
import com.xxmrk888ytxx.mainscreen.model.CreateShortcutDialogState
import com.xxmrk888ytxx.mainscreen.model.Device
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.MainScreenSideEffect
import com.xxmrk888ytxx.mainscreen.model.Permission
import com.xxmrk888ytxx.mainscreen.model.PermissionBannerItem
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import com.xxmrk888ytxx.mainscreen.model.ShortcutOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val provideSavedDevices: ProvideSavedDevices,
    private val unlockRequestContract: SendUnlockRequestContract,
    private val createShortcutContract: CreateShortcutContract,
    private val permissionContract: PermissionContract,
    private val manageDevicesRemovedBannerStateContract: ManageDevicesRemovedBannerStateContract
) : SideEffectPortalViewModel<ScreenState, MainScreenEvent>(ScreenState()) {

    private val isLoading = MutableStateFlow(false)
    private val createShortcutDialogState =
        MutableStateFlow<CreateShortcutDialogState>(CreateShortcutDialogState.Hidden)

    private val permissionBannerItemListState =
        MutableStateFlow<List<PermissionBannerItem>>(emptyList())


    override val state: StateFlow<ScreenState> =
        combine(
            provideSavedDevices.devices,
            isLoading,
            createShortcutDialogState,
            permissionBannerItemListState,
            manageDevicesRemovedBannerStateContract.devicesRemovedBannerState
        ) { deviceList, isLoading, createShortcutDialogState, permissionBannerItemList, provideDevicesRemovedBannerStateContract ->
            ScreenState(
                devices = deviceList,
                isLoading = isLoading,
                createShortcutDialogState = createShortcutDialogState,
                permissionBannerItemList = permissionBannerItemList,
                devicesRemovedBannerState = provideDevicesRemovedBannerStateContract
            )
        }.stateWhileSubscribed()


    override fun handleEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.AddNewDevice -> sendNavigationAction { fromMainScreenToAddNewDeviceScreen() }
            is MainScreenEvent.SendUnlockRequest -> sendUnlockRequest(event.device)
            is MainScreenEvent.ToDeviceDetailsScreen -> sendNavigationAction {
                fromMainScreenToDeviceConfigurationScreen(
                    event.deviceId
                )
            }

            is MainScreenEvent.ShowCreateShortcutModelDialog -> showCreateShortcutDialog(event.device)
            is MainScreenEvent.DismissCreateShortcutModelDialog -> hideCreateShortcutDialog()
            is MainScreenEvent.OnIsRequiredBiometricUnlockStateChanged -> updateCreateShortcutDialogState {
                it.copy(
                    isRequiredBiometricUnlock = event.isRequiredBiometricUnlock
                )
            }

            MainScreenEvent.CreateShortcut -> createShortcut()
            MainScreenEvent.RequestFullScreenIntentPermission -> requestFullScreenIntentPermission()
            MainScreenEvent.RequestNearbyDevicesPermission -> sideEffect.tryEmit(
                MainScreenSideEffect.RequestNearbyDevicesPermission
            )

            MainScreenEvent.RequestNotificationPermission -> sideEffect.tryEmit(MainScreenSideEffect.RequestNotificationPermission)
            is MainScreenEvent.PermissionGranted -> checkPermission()
            MainScreenEvent.ActivityInOnResumeState -> checkPermission()
            MainScreenEvent.DismissDevicesRemovedBanner -> dismissDismissDevicesRemovedBanner()
        }
    }

    private fun dismissDismissDevicesRemovedBanner() = viewModelScope.launch {
        manageDevicesRemovedBannerStateContract.resetState()
    }

    private fun requestFullScreenIntentPermission() = viewModelScope.launch {
        permissionContract.requestShowFullScreenIntentPermission()
    }

    private fun createShortcut() {
        if (isLoading.value) return
        val createShortcutDialogState =
            createShortcutDialogState.value as? CreateShortcutDialogState.Showed ?: return
        val shortcutOption = ShortcutOption(
            createShortcutDialogState.device,
            createShortcutDialogState.isRequiredBiometricUnlock
        )
        handleEvent(MainScreenEvent.DismissCreateShortcutModelDialog)
        isLoading.update { true }
        viewModelScope.launch {
            createShortcutContract.createShortcutContract(shortcutOption)
                .onSuccess { }
                .onFailure { error ->
                    fastDebugLog(error)
                    val errorMessage = when (error) {
                        is LauncherNotSupportShortcutException -> {
                            uiText(R.string.your_home_screen_launcher_doesn_t_support_shortcuts)
                        }

                        else -> uiText(R.string.failed_to_create_shortcut_please_try_again)
                    }
                    sendToastSideEffect(errorMessage)
                }
        }.invokeOnCompletion { isLoading.update { false } }
    }

    private fun updateCreateShortcutDialogState(onUpdate: (CreateShortcutDialogState.Showed) -> CreateShortcutDialogState.Showed) {
        createShortcutDialogState.update {
            if (it !is CreateShortcutDialogState.Showed) return@update it
            onUpdate(it)
        }
    }

    private fun showCreateShortcutDialog(device: Device) {
        createShortcutDialogState.value = CreateShortcutDialogState.Showed(device)
    }

    private fun hideCreateShortcutDialog() {
        createShortcutDialogState.value = CreateShortcutDialogState.Hidden
    }

    private fun sendUnlockRequest(device: Device) {
        if (isLoading.value) return
        isLoading.value = true
        viewModelScope.launch {
            unlockRequestContract.unlock(device)
                .onSuccess { sendToastSideEffect(uiText(R.string.device_unlocked)) }
                .onFailure { sendToastSideEffect(uiText(R.string.failed_to_unlock_device)) }
        }.invokeOnCompletion { isLoading.value = false }
    }

    private fun checkPermission() = viewModelScope.launch {
        val permissionBannerItems = permissionContract.getDeniedPermissions().map {
            when (it) {
                Permission.NearbyDevices -> PermissionBannerItem(
                    title = R.string.grant_nearby_devices_permission.uiText(),
                    description = R.string.this_permission_is_required_by_the_app_if_you_use_bluetooth_to_unlock_your_device.uiText(),
                    iconRes = R.drawable.nearby,
                    eventForRequestPermission = MainScreenEvent.RequestNearbyDevicesPermission
                )

                Permission.Notification -> PermissionBannerItem(
                    title = R.string.grant_notification_permission.uiText(),
                    description = R.string.the_app_requires_this_permission_to_send_notifications_regarding_unlock_requests_as_well_as_to_function_correctly_in_the_background_and_to_indicate_that_it_is_running.uiText(),
                    iconRes = R.drawable.notifications,
                    eventForRequestPermission = MainScreenEvent.RequestNotificationPermission
                )

                Permission.ShowSystemAlertPermission -> PermissionBannerItem(
                    title = R.string.grant_grant_display_over_other_apps_permission.uiText(),
                    description = R.string.this_permission_is_required_to_display_device_unlock_prompts_on_top_of_all_windows_this_allows_you_to_instantly_grant_or_deny_access_without_unlocking_your_phone_or_opening_the_app.uiText(),
                    iconRes = R.drawable.open_in_full,
                    eventForRequestPermission = MainScreenEvent.RequestFullScreenIntentPermission
                )
            }
        }
        permissionBannerItemListState.value = permissionBannerItems
    }

    init {
        checkPermission()
    }
}