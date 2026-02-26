package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.portal.unlockService.WifiDriver
import com.xxmrk888ytxx.unlockservice.qualifier.WifiNetworkDriver
import com.xxmrk888ytxx.unlockservice.wifiService.NetworkDriver
import dagger.Binds
import dagger.Module

@Module
interface UnlockServiceModule {
    @Binds
    @WifiNetworkDriver
    fun bindsWifiNetworkDriver(wifiDriver: WifiDriver): NetworkDriver
}