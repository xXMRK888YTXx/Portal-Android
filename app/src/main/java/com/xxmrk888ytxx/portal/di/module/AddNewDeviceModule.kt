package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToWifiDeviceContract
import com.xxmrk888ytxx.addnewdevicescreen.contract.UpdateDeviceSettingsContract
import com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen.ConnectToWifiDeviceContractImpl
import com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen.UpdateDeviceSettingsContractImpl
import dagger.Binds
import dagger.Module

@Module
interface AddNewDeviceModule {
    @Binds
    fun bindConnectToWifiDeviceContract(connectToWifiDeviceContractImpl: ConnectToWifiDeviceContractImpl): ConnectToWifiDeviceContract

    @Binds
    fun bindsUpdateDeviceSettingsContract(
        updateDeviceSettingsContractImpl: UpdateDeviceSettingsContractImpl
    ) : UpdateDeviceSettingsContract
}