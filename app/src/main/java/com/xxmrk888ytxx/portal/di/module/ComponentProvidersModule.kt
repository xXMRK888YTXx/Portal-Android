package com.xxmrk888ytxx.portal.di.module

import android.app.Activity
import android.app.Service
import dagger.Module
import dagger.multibindings.Multibinds

@Module
interface ComponentProvidersModule {
    @Multibinds
    fun provideActivities(): Map<Class<out Activity>, @JvmSuppressWildcards Activity>

    @Multibinds
    fun provideServices(): Map<Class<out Service>, @JvmSuppressWildcards Service>
}