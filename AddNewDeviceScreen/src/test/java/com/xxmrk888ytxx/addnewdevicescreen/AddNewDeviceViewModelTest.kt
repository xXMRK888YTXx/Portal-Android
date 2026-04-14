package com.xxmrk888ytxx.addnewdevicescreen

import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToBluetoothDeviceContract
import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToWifiDeviceContract
import com.xxmrk888ytxx.addnewdevicescreen.contract.ProvideBluetoothPairedDevices
import com.xxmrk888ytxx.addnewdevicescreen.contract.ScanQrCodeContract
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenUiEvent
import com.xxmrk888ytxx.addnewdevicescreen.model.ScreenState
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddNewDeviceViewModelTest {

    private val connectToWifiDeviceContract: ConnectToWifiDeviceContract = mockk()
    private val scanQrCodeContract: ScanQrCodeContract = mockk()
    private val provideBluetoothPairedDevices: ProvideBluetoothPairedDevices = mockk()
    private val connectToBluetoothDeviceContract: ConnectToBluetoothDeviceContract = mockk()

    private lateinit var viewModel: AddNewDeviceViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddNewDeviceViewModel(
            connectToWifiDeviceContract,
            scanQrCodeContract,
            provideBluetoothPairedDevices,
            connectToBluetoothDeviceContract
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is NoSelectedType`() {
        assertTrue(viewModel.state.value is ScreenState.NoSelectedType)
    }

    @Test
    fun `when SelectedWifi event, state changes to Wifi`() {
        viewModel.handleEvent(AddNewDeviceScreenUiEvent.SelectedWifi)
        assertTrue(viewModel.state.value is ScreenState.Wifi)
    }

    @Test
    fun `when SelectedBluetooth event, state changes to Bluetooth`() {
        viewModel.handleEvent(AddNewDeviceScreenUiEvent.SelectedBluetooth)
        assertTrue(viewModel.state.value is ScreenState.Bluetooth)
    }

    @Test
    fun `when HostTextUpdated event, host is updated in Wifi state`() {
        viewModel.handleEvent(AddNewDeviceScreenUiEvent.SelectedWifi)
        val testHost = "192.168.1.1"
        viewModel.handleEvent(AddNewDeviceScreenUiEvent.HostTextUpdated(testHost))
        
        val currentState = viewModel.state.value as ScreenState.Wifi
        assertTrue(currentState.host == testHost)
    }
}
