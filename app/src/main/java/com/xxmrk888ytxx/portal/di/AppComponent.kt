package com.xxmrk888ytxx.portal.di

import android.content.Context
import com.xxmrk888ytxx.mydictionary.DI.scope.AppScope
import com.xxmrk888ytxx.portal.view.MainActivity
import com.xxmrk888ytxx.portal.di.module.CoreModule
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        CoreModule::class,
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