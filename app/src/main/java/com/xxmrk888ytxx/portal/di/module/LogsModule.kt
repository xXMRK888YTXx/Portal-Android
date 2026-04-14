package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.logsscreen.contract.ProvideLogsContract
import com.xxmrk888ytxx.portal.providedContract.logsScreen.ProvideLogsContractImpl
import dagger.Binds
import dagger.Module

@Module
interface LogsModule {
    @Binds
    fun bindsProvideLogsContract(
        provideLogsContractImpl: ProvideLogsContractImpl
    ) : ProvideLogsContract
}