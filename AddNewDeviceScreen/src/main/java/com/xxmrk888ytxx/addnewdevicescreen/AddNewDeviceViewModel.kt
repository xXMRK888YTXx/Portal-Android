package com.xxmrk888ytxx.addnewdevicescreen

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToBluetoothDeviceContract
import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToWifiDeviceContract
import com.xxmrk888ytxx.addnewdevicescreen.contract.ProvideBluetoothPairedDevices
import com.xxmrk888ytxx.addnewdevicescreen.contract.ScanQrCodeContract
import com.xxmrk888ytxx.addnewdevicescreen.exception.BluetoothDisabledException
import com.xxmrk888ytxx.addnewdevicescreen.exception.BluetoothPermissionNotGrantedException
import com.xxmrk888ytxx.addnewdevicescreen.exception.QRScanCanceledException
import com.xxmrk888ytxx.addnewdevicescreen.exception.QRScannerNotDownloadedException
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenSideEffect
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenUiEvent
import com.xxmrk888ytxx.addnewdevicescreen.model.BluetoothDevice
import com.xxmrk888ytxx.addnewdevicescreen.model.BluetoothState
import com.xxmrk888ytxx.addnewdevicescreen.model.Page
import com.xxmrk888ytxx.addnewdevicescreen.model.ScreenState
import com.xxmrk888ytxx.addnewdevicescreen.model.Validator
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.uiText.buildUiText
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddNewDeviceViewModel @Inject constructor(
    private val connectToWifiDeviceContract: ConnectToWifiDeviceContract,
    private val scanQrCodeContract: ScanQrCodeContract,
    private val provideBluetoothPairedDevices: ProvideBluetoothPairedDevices,
    private val connectToBluetoothDeviceContract: ConnectToBluetoothDeviceContract
) :
    SideEffectPortalViewModel<ScreenState, AddNewDeviceScreenUiEvent>(
        ScreenState.NoSelectedType
    ) {
    override fun handleEvent(event: AddNewDeviceScreenUiEvent) {
        when (event) {
            is AddNewDeviceScreenUiEvent.SelectedBluetooth -> bluetoothSelected()
            is AddNewDeviceScreenUiEvent.SelectedWifi -> wifiSelected()
            is AddNewDeviceScreenUiEvent.NextPage -> nextPage(event.currentPage)
            is AddNewDeviceScreenUiEvent.PreviousPage -> previousPage(event.currentPage)
            is AddNewDeviceScreenUiEvent.HostTextUpdated -> hostTextUpdated(event.text)
            is AddNewDeviceScreenUiEvent.PairCodeTextUpdated -> pairCodeUpdated(event.text)
            is AddNewDeviceScreenUiEvent.ConnectToDevice -> {
                when (val state = state.value) {
                    is ScreenState.Bluetooth -> connectToBluetoothDevice(state)
                    is ScreenState.Wifi -> connectToWifiDevice(state)
                    else -> {}
                }
            }

            is AddNewDeviceScreenUiEvent.FinishConfiguration -> sendNavigateUpSideEffect()
            is AddNewDeviceScreenUiEvent.DeviceNameTextUpdated -> updateDeviceName(event.text)
            is AddNewDeviceScreenUiEvent.OnScanQrCodeClicked -> requestQRScan()
            is AddNewDeviceScreenUiEvent.RequestBluetoothPermission -> sideEffect.tryEmit(
                AddNewDeviceScreenSideEffect.RequestBluetoothPermission
            )

            is AddNewDeviceScreenUiEvent.UpdateBluetoothState -> updateBluetoothData()
            is AddNewDeviceScreenUiEvent.EnableBluetooth -> sideEffect.tryEmit(
                AddNewDeviceScreenSideEffect.EnableBluetooth
            )

            is AddNewDeviceScreenUiEvent.OpenBluetoothSettings -> sideEffect.tryEmit(
                AddNewDeviceScreenSideEffect.OpenBluetoothSettings
            )

            is AddNewDeviceScreenUiEvent.OnBluetoothDeviceSelected -> updateSelectedBluetoothDevice(
                event.device
            )
        }
    }

    private fun connectToBluetoothDevice(state: ScreenState.Bluetooth) {
        if (_state.value.isLoading) return
        val selectedDevice = state.selectedDevice ?: return
        updateLoadingState(true)
        viewModelScope.launch {
            connectToBluetoothDeviceContract.connect(
                state.deviceName,
                state.pairCode,
                state.selectedDevice
            )
                .onSuccessConnectHandler()
                .onFailure {
                    val uiText =
                        buildUiText { provider -> provider.provide(uiText(R.string.unable_to_connect_to_the)) + " " + selectedDevice.name }
                    sendToastSideEffect(uiText)
                }
        }.invokeOnCompletion { updateLoadingState(false) }
    }

    private fun updateSelectedBluetoothDevice(bluetoothDevice: BluetoothDevice) {
        updateBluetoothState { it.copy(selectedDevice = bluetoothDevice) }
    }

    private fun requestQRScan() = viewModelScope.launch {
        scanQrCodeContract.requestScan()
            .onSuccess { scanResult ->
                when (state.value) {
                    is ScreenState.Bluetooth -> {

                        val bluetoothState = (state.value as? ScreenState.Bluetooth)?.pairedDevices

                        if (bluetoothState is BluetoothState.Success) {
                            bluetoothState.pairedDevices
                                .firstOrNull { it.macAddress == scanResult.macAddress }
                                ?.let { device ->
                                    updateSelectedBluetoothDevice(device)
                                }
                        }
                        updateDeviceName(scanResult.deviceName)
                        pairCodeUpdated(scanResult.pairCode.toString())
                    }

                    is ScreenState.Wifi -> {
                        updateDeviceName(scanResult.deviceName)
                        hostTextUpdated(scanResult.host ?: "")
                        pairCodeUpdated(scanResult.pairCode.toString())
                    }

                    else -> Unit
                }
            }
            .onFailure {
                val uiText = when (it) {
                    is QRScanCanceledException -> uiText(R.string.cancelled_by_the_user)
                    is QRScannerNotDownloadedException -> uiText(R.string.the_qr_code_scanner_has_not_been_loaded)
                    else -> uiText(R.string.an_error_occurred_during_scanning)
                }
                sendToastSideEffect(uiText)
            }
    }

    private fun connectToWifiDevice(value: ScreenState.Wifi) {
        updateLoadingState(true)
        viewModelScope.launch {
            connectToWifiDeviceContract.connectAndDeviceId(
                value.deviceName,
                value.host,
                value.pairCode
            )
                .onSuccessConnectHandler()
                .onFailure {
                    sendToastSideEffect(uiText = uiText(R.string.unable_to_establish_connection))
                }
        }.invokeOnCompletion { updateLoadingState(false) }
    }

    private fun pairCodeUpdated(text: String) {
        if (text.length > 6 || !text.isDigitsOnly()) return
        when (_state.value) {
            is ScreenState.Bluetooth -> updateBluetoothState { it.copy(pairCode = text) }
            is ScreenState.Wifi -> updateWifiState { it.copy(pairCode = text) }
            else -> {}
        }
    }

    private fun hostTextUpdated(text: String) {
        val updatedText = text.replace(oldValue = ",", newValue = ".", ignoreCase = true)
        updateWifiState { it.copy(host = updatedText) }
    }

    private fun bluetoothSelected() {
        _state.value = ScreenState.Bluetooth()
        handleEvent(AddNewDeviceScreenUiEvent.UpdateBluetoothState)
    }

    private fun updateBluetoothData() = withLoading {
        provideBluetoothPairedDevices.getPairedDevices()
            .onSuccess { devices ->
                val currentSelectedDevice = (state.value as? ScreenState.Bluetooth)?.selectedDevice
                if (!devices.contains(currentSelectedDevice)) {
                    updateBluetoothState { state -> state.copy(selectedDevice = null) }
                }
                updateBluetoothState { state ->
                    state.copy(
                        pairedDevices = BluetoothState.Success(
                            devices
                        )
                    )
                }
            }
            .onFailure {
                updateBluetoothState { state -> state.copy(selectedDevice = null) }
                fastDebugLog(it)
                when (it) {
                    is BluetoothDisabledException -> updateBluetoothState { state ->
                        state.copy(
                            pairedDevices = BluetoothState.Disabled
                        )
                    }

                    is BluetoothPermissionNotGrantedException -> updateBluetoothState { state ->
                        state.copy(
                            pairedDevices = BluetoothState.PermissionDenied
                        )
                    }

                    else -> updateBluetoothState { state -> state.copy(pairedDevices = BluetoothState.NotSupported) }
                }
            }
    }

    private fun wifiSelected() {
        _state.value = ScreenState.Wifi()
    }

    private fun updateBluetoothState(onUpdate: (ScreenState.Bluetooth) -> ScreenState.Bluetooth) {
        val currentState = _state.value as? ScreenState.Bluetooth ?: return
        val newState = onUpdate(currentState)
        _state.update { newState.copy(isDataValid = Validator.isBluetoothStateValid(newState)) }
    }

    private fun nextPage(currentPage: Page) {
        when (currentPage) {
            Page.SELECT_TYPE -> when (state.value) {
                is ScreenState.Bluetooth -> sideEffect.tryEmit(AddNewDeviceScreenSideEffect.ToBluetoothConfigurationPage)
                is ScreenState.Wifi -> sideEffect.tryEmit(AddNewDeviceScreenSideEffect.ToWifiConfigurationPage)
                else -> {}
            }

            else -> {}
        }
    }

    private fun previousPage(currentPage: Page) {
        when (currentPage.id) {
            0 -> sendNavigationAction { navigateUp() }
            else -> sideEffect.tryEmit(AddNewDeviceScreenSideEffect.ScrollToPage(Page.SELECT_TYPE.id))
        }
    }

    private fun updateWifiState(onUpdate: (ScreenState.Wifi) -> ScreenState.Wifi) {
        val currentState = _state.value as? ScreenState.Wifi ?: return
        val newState = onUpdate(currentState)
        _state.update { newState.copy(isDataValid = Validator.isWifiStateValid(newState)) }
    }

    private fun updateLoadingState(newState: Boolean) {
        when (val currentState = state.value) {
            is ScreenState.Bluetooth -> _state.update { currentState.copy(isLoading = newState) }
            is ScreenState.Wifi -> _state.update { currentState.copy(isLoading = newState) }
            else -> {}
        }
    }

    private fun updateDeviceName(newName: String) {
        when (val currentState = state.value) {
            is ScreenState.Bluetooth -> _state.update { currentState.copy(deviceName = newName) }
            is ScreenState.Wifi -> _state.update { currentState.copy(deviceName = newName) }
            else -> {}
        }
    }

    private fun withLoading(block: suspend () -> Unit) {
        updateLoadingState(true)
        viewModelScope.launch {
            block()
        }.invokeOnCompletion { updateLoadingState(false) }
    }

    private fun Result<String>.onSuccessConnectHandler(): Result<String> {
        onSuccess { deviceId ->
            sendToastSideEffect(uiText(R.string.successfully_connected))
            sendNavigationAction {
                fromAddNewDeviceScreenToDeviceConfigurationScreen(
                    deviceId
                )
            }
        }
        return this
    }
}