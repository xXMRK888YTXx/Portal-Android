package com.xxmrk888ytxx.portal.di

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import com.xxmrk888ytxx.portal.di.module.ActivityBindsModule
import com.xxmrk888ytxx.portal.di.module.BroadcastReceiverBindsModule
import com.xxmrk888ytxx.portal.di.module.DataModule
import com.xxmrk888ytxx.portal.di.module.ServiceBindsModule
import com.xxmrk888ytxx.portal.di.scope.AppScope
import dagger.BindsInstance
import dagger.Component
import javax.inject.Provider

/**
 * App-scoped Dagger graph for the Wear OS application.
 */
@Component(
    modules = [
        ActivityBindsModule::class,
        ServiceBindsModule::class,
        BroadcastReceiverBindsModule::class,
        DataModule::class
    ]
)
@AppScope
interface AppComponent {

    val activityProviderMap: Map<Class<out Activity>, @JvmSuppressWildcards Provider<Activity>>
    val serviceProviderMap: Map<Class<out Service>, @JvmSuppressWildcards Provider<Service>>
    val broadcastReceiverProviderMap: Map<Class<out BroadcastReceiver>, @JvmSuppressWildcards Provider<BroadcastReceiver>>

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
