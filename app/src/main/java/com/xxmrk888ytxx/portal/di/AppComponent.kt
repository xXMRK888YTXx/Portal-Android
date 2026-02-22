package com.xxmrk888ytxx.portal.di

import android.content.Context
import com.xxmrk888ytxx.mydictionary.DI.scope.AppScope
import com.xxmrk888ytxx.portal.di.module.AddNewDeviceModule
import com.xxmrk888ytxx.portal.view.MainActivity
import com.xxmrk888ytxx.portal.di.module.CoreModule
import com.xxmrk888ytxx.portal.di.module.DataModule
import com.xxmrk888ytxx.portal.di.module.DomainModule
import com.xxmrk888ytxx.portal.di.module.MainScreenModule
import com.xxmrk888ytxx.portal.di.module.OnboardingScreenModule
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        CoreModule::class,
        DataModule::class,
        DomainModule::class,
        OnboardingScreenModule::class,
        AddNewDeviceModule::class,
        MainScreenModule::class
    ]
)
@AppScope
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context:Context) : AppComponent
    }
}