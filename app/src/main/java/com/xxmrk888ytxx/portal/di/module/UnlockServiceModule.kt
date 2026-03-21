package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.portal.data.unlockService.WifiDriver
import com.xxmrk888ytxx.unlockservice.qualifier.WifiNetworkDriver
import com.xxmrk888ytxx.unlockservice.core.NetworkDriver
import dagger.Binds
import dagger.Module

@Module
interface UnlockServiceModule {
    @Binds
    @WifiNetworkDriver
    fun bindsWifiNetworkDriver(wifiDriver: WifiDriver): NetworkDriver
}