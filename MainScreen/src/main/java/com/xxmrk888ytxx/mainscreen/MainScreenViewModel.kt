package com.xxmrk888ytxx.mainscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.formatToMacAddress
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import com.xxmrk888ytxx.mainscreen.contract.CreateShortcutContract
import com.xxmrk888ytxx.mainscreen.contract.ManageDevicesRemovedBannerStateContract
import com.xxmrk888ytxx.mainscreen.contract.PermissionContract
import com.xxmrk888ytxx.mainscreen.contract.ProvideSavedDevices
import com.xxmrk888ytxx.mainscreen.contract.SaveWOLMacAddress
import com.xxmrk888ytxx.mainscreen.contract.SendUnlockRequestContract
import com.xxmrk888ytxx.mainscreen.contract.SendWOLContract
import com.xxmrk888ytxx.mainscreen.contract.SettingsProvider
import com.xxmrk888ytxx.mainscreen.exception.LauncherNotSupportShortcutException
import com.xxmrk888ytxx.mainscreen.model.Device
import com.xxmrk888ytxx.mainscreen.model.DeviceType
import com.xxmrk888ytxx.mainscreen.model.DevicesRemovedBannerState
import com.xxmrk888ytxx.mainscreen.model.DialogState
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.MainScreenSideEffect
import com.xxmrk888ytxx.mainscreen.model.Permission
import com.xxmrk888ytxx.mainscreen.model.PermissionBannerItem
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import com.xxmrk888ytxx.mainscreen.model.ShortcutOption
import kotlinx.collections.immutable.ImmutableList
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
    private val manageDevicesRemovedBannerStateContract: ManageDevicesRemovedBannerStateContract,
    private val saveWOLMacAddress: SaveWOLMacAddress,
    private val sendWOLContract: SendWOLContract,
    private val settingsProvider: SettingsProvider
) : SideEffectPortalViewModel<ScreenState, MainScreenEvent>(ScreenState()) {

    private val isLoading = MutableStateFlow(false)
    private val dialogState =
        MutableStateFlow<DialogState>(DialogState.Hidden)

    private val permissionBannerItemListState =
        MutableStateFlow<List<PermissionBannerItem>>(emptyList())


    @Suppress("UNCHECKED_CAST")
    override val state: StateFlow<ScreenState> =
        combine(
            provideSavedDevices.devices,
            isLoading,
            dialogState,
            permissionBannerItemListState,
            manageDevicesRemovedBannerStateContract.devicesRemovedBannerState,
            settingsProvider.isBiometricProtectionAvailable,
            settingsProvider.isUnsafeUnlockTypesDisabled
        ) { flowArray ->
            val deviceList = flowArray[0] as ImmutableList<Device>
            val isLoading = flowArray[1] as Boolean
            val createShortcutDialogState = flowArray[2] as DialogState
            val permissionBannerItemList = flowArray[3] as List<PermissionBannerItem>
            val provideDevicesRemovedBannerStateContract = flowArray[4] as DevicesRemovedBannerState
            val isBiometricProtectionAvailable = flowArray[5] as Boolean
            val isUnsafeUnlockTypesDisabled = flowArray[6] as Boolean
            ScreenState(
                devices = deviceList,
                isLoading = isLoading,
                dialogState = createShortcutDialogState,
                permissionBannerItemList = permissionBannerItemList,
                devicesRemovedBannerState = provideDevicesRemovedBannerStateContract,
                isBiometricProtectionAvailable = isBiometricProtectionAvailable,
                isUnsafeUnlockTypesDisabled = isUnsafeUnlockTypesDisabled
            )
        }.stateWhileSubscribed()


    override fun handleEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.AddNewDevice -> sendNavigationAction { fromMainScreenToAddNewDeviceScreen() }
            is MainScreenEvent.SendUnlockRequest -> sendUnlockRequest(event.device)
            is MainScreenEvent.ToDeviceDetailsScreen -> sendNavigationAction {
                fromMainScreenToDeviceConfigurationScreen(
                    event.clientId
                )
            }

            is MainScreenEvent.ShowCreateShortcutModelDialog -> showCreateShortcutDialog(event.device)
            is MainScreenEvent.DismissDialog -> hideCreateShortcutDialog()
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
            is MainScreenEvent.WakeUpOnLANClicked -> showWACRequestDialog(event.device)
            is MainScreenEvent.OnMacAddressChanged -> updateMacAddressText(event.newText)
            MainScreenEvent.SaveWOLMacAddress -> saveWALMacAddress()
            is MainScreenEvent.OnIsTryToSendEnabledChanged -> updateWALRequestDialog {
                it.copy(
                    isTryToSendUnlockRequestEnabled = event.newState
                )
            }

            MainScreenEvent.SendWOLRequest -> sendWOLRequest()
            is MainScreenEvent.OnIsRequiredSendWOLRequestChanged -> updateCreateShortcutDialogState {
                it.copy(isWolEnabled = event.newValue)
            }
        }
    }

    private fun sendWOLRequest() {
        if (isLoading.value) return
        isLoading.value = true
        viewModelScope.launch {
            val dialogState = (dialogState.value as? DialogState.WALRequestDialog) ?: return@launch
            this@MainScreenViewModel.dialogState.value = DialogState.Hidden
            sendWOLContract.sendRequest(
                dialogState.device,
                dialogState.isTryToSendUnlockRequestEnabled
            ).onSuccess {
                sendToastSideEffect(uiText(R.string.wol_request_sent))
            }.onFailure {
                sendToastSideEffect(R.string.error_during_sending_the_wol_request.uiText())
            }
        }.invokeOnCompletion { isLoading.value = false }
    }

    private fun saveWALMacAddress() {
        if (isLoading.value) return
        viewModelScope.launch {
            val dialogState =
                (dialogState.value as? DialogState.EnterMacAddressDialog) ?: return@launch
            val macAddress = dialogState.enteredMac.formatToMacAddress() ?: return@launch
            this@MainScreenViewModel.dialogState.value = DialogState.Hidden
            saveWOLMacAddress.save(dialogState.device.clientId, macAddress)
            sendToastSideEffect(R.string.mac_address_saved.uiText())
        }.invokeOnCompletion { isLoading.value = false }
    }

    private fun updateMacAddressText(newText: String) {
        if (!newText.isValidMacInput()) return
        updateEnterMacAddressDialog {
            it.copy(
                enteredMac = newText,
                isValidateMacAddress = newText.length == 12
            )
        }
    }

    private fun showWACRequestDialog(device: Device) {
        if (device.deviceType != DeviceType.WIFI) return
        if (!device.isWakeUpOnLanAvailable) {
            dialogState.value = DialogState.EnterMacAddressDialog(device = device)
            return
        }
        dialogState.value = DialogState.WALRequestDialog(device = device)
    }

    private fun dismissDismissDevicesRemovedBanner() = viewModelScope.launch {
        manageDevicesRemovedBannerStateContract.resetState()
    }

    private fun requestFullScreenIntentPermission() = viewModelScope.launch {
        permissionContract.requestShowFullScreenIntentPermission()
    }

    private fun createShortcut() {
        if (isLoading.value) return
        val dialogState =
            dialogState.value as? DialogState.ShortcutDialog ?: return
        val shortcutOption = ShortcutOption(
            device = dialogState.device,
            isRequiredBiometricUnlock = dialogState.isRequiredBiometricUnlock,
            isSendWOLRequest = dialogState.isWolEnabled
        )
        handleEvent(MainScreenEvent.DismissDialog)
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


    private fun updateWALRequestDialog(onUpdate: (DialogState.WALRequestDialog) -> DialogState.WALRequestDialog) {
        dialogState.update {
            if (it !is DialogState.WALRequestDialog) return@update it
            onUpdate(it)
        }
    }

    private fun updateEnterMacAddressDialog(onUpdate: (DialogState.EnterMacAddressDialog) -> DialogState.EnterMacAddressDialog) {
        dialogState.update {
            if (it !is DialogState.EnterMacAddressDialog) return@update it
            onUpdate(it)
        }
    }

    private fun updateCreateShortcutDialogState(onUpdate: (DialogState.ShortcutDialog) -> DialogState.ShortcutDialog) {
        dialogState.update {
            if (it !is DialogState.ShortcutDialog) return@update it
            onUpdate(it)
        }
    }

    private fun showCreateShortcutDialog(device: Device) {
        dialogState.value = DialogState.ShortcutDialog(
            device,
            isWOLAvailable = device.isWakeUpOnLanAvailable,
            isBiometricUnlockAvailable = state.value.isBiometricProtectionAvailable,
            isWOLVisible = device.deviceType == DeviceType.WIFI,
            isUnsafeUnlockTypesDisabled = state.value.isUnsafeUnlockTypesDisabled,
            isRequiredBiometricUnlock = state.value.isUnsafeUnlockTypesDisabled
        )
    }

    private fun hideCreateShortcutDialog() {
        dialogState.value = DialogState.Hidden
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

    fun String.isValidMacInput(): Boolean {
        // Разрешаем только 0-9, a-f, A-F и двоеточие
        val allowedChars = "0123456789abcdefABCDEF"
        if (this.any { it !in allowedChars }) return false

        return length <= 12
    }

    init {
        checkPermission()
    }
}