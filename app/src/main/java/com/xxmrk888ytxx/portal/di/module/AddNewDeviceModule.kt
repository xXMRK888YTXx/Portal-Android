package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToWifiDeviceContract
import com.xxmrk888ytxx.addnewdevicescreen.contract.ProvideBluetoothPairedDevices
import com.xxmrk888ytxx.addnewdevicescreen.contract.ScanQrCodeContract
import com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen.ConnectToWifiDeviceContractImpl
import com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen.ProvideBluetoothPairedDevicesImpl
import com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen.ScanQrCodeContractImpl
import dagger.Binds
import dagger.Module

@Module
interface AddNewDeviceModule {
    @Binds
    fun bindConnectToWifiDeviceContract(connectToWifiDeviceContractImpl: ConnectToWifiDeviceContractImpl): ConnectToWifiDeviceContract

    @Binds
    fun bindScanQrCodeContract(
        scanQrCodeContractImpl: ScanQrCodeContractImpl
    ) : ScanQrCodeContract

    @Binds
    fun bindsProvideBluetoothPairedDevices(
        provideBluetoothPairedDevicesImpl: ProvideBluetoothPairedDevicesImpl
    ) : ProvideBluetoothPairedDevices
}