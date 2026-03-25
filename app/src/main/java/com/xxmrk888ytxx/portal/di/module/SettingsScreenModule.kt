package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.portal.providedContract.settingsScreen.ProvideSettingsStateImpl
import com.xxmrk888ytxx.settingsscreen.contract.ProvideSettingsState
import dagger.Binds
import dagger.Module

@Module
interface SettingsScreenModule {
    @Binds
    fun bindsProvideSettingsState(
        provideSettingsStateImpl: ProvideSettingsStateImpl
    ) : ProvideSettingsState
}