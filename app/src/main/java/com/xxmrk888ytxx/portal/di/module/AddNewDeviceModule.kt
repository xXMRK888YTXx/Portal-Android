package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToWifiDeviceContract
import com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen.ConnectToWifiDeviceContractImpl
import dagger.Binds
import dagger.Module

@Module
interface AddNewDeviceModule {
    @Binds
    fun bindConnectToWifiDeviceContract(connectToWifiDeviceContractImpl: ConnectToWifiDeviceContractImpl): ConnectToWifiDeviceContract
}