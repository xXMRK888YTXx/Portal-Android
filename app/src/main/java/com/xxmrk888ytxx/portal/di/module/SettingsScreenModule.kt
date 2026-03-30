package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.portal.providedContract.settingsScreen.BiometricProtectionAvailableStateProviderImpl
import com.xxmrk888ytxx.portal.providedContract.settingsScreen.ChangeSettingsContractImpl
import com.xxmrk888ytxx.portal.providedContract.settingsScreen.OpenLinkContractImpl
import com.xxmrk888ytxx.portal.providedContract.settingsScreen.ProvideSettingsStateImpl
import com.xxmrk888ytxx.settingsscreen.contract.BiometricProtectionAvailableStateProvider
import com.xxmrk888ytxx.settingsscreen.contract.ChangeSettingsContract
import com.xxmrk888ytxx.settingsscreen.contract.OpenLinkContract
import com.xxmrk888ytxx.settingsscreen.contract.ProvideSettingsState
import dagger.Binds
import dagger.Module

@Module
interface SettingsScreenModule {
    @Binds
    fun bindsProvideSettingsState(
        provideSettingsStateImpl: ProvideSettingsStateImpl
    ) : ProvideSettingsState

    @Binds
    fun bindsChangeSettingsContract(
        changeSettingsContractImpl: ChangeSettingsContractImpl
    ) : ChangeSettingsContract

    @Binds
    fun bindsBiometricProtectionAvailableStateProvider(
        biometricProtectionAvailableStateProviderImpl: BiometricProtectionAvailableStateProviderImpl
    ) : BiometricProtectionAvailableStateProvider

    @Binds
    fun bindsOpenLinkContract(
        openLinkContractImpl: OpenLinkContractImpl
    ) : OpenLinkContract
}