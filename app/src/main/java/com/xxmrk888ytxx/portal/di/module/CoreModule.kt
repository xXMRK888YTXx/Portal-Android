package com.xxmrk888ytxx.portal.di.module

import android.content.Context
import com.xxmrk888ytxx.coreandroid.AndroidLogger
import com.xxmrk888ytxx.coreandroid.BaseToastManager
import com.xxmrk888ytxx.coreandroid.Logger
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.portal.di.scope.AppScope
import dagger.Module
import dagger.Provides

@Module
class CoreModule {

    @Provides
    @AppScope
    fun provideLogger() : Logger = AndroidLogger

    @Provides
    @AppScope
    fun provideToastManager(context: Context) : ToastManager {
        return BaseToastManager(context)
    }
}