package com.xxmrk888ytxx.portal.di

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import com.xxmrk888ytxx.portal.data.BiometricEnvironmentObserver
import com.xxmrk888ytxx.portal.data.UnsafeUnlockTypesStateObserver
import com.xxmrk888ytxx.portal.di.module.ActivityBindsModule
import com.xxmrk888ytxx.portal.di.module.AddNewDeviceModule
import com.xxmrk888ytxx.portal.di.module.BroadcastReceiverBindsModule
import com.xxmrk888ytxx.portal.di.module.ComponentProvidersModule
import com.xxmrk888ytxx.portal.di.module.CoreModule
import com.xxmrk888ytxx.portal.di.module.DataModule
import com.xxmrk888ytxx.portal.di.module.DeviceConfigurationScreenModule
import com.xxmrk888ytxx.portal.di.module.DomainModule
import com.xxmrk888ytxx.portal.di.module.LogsModule
import com.xxmrk888ytxx.portal.di.module.MainScreenModule
import com.xxmrk888ytxx.portal.di.module.OnboardingScreenModule
import com.xxmrk888ytxx.portal.di.module.ServiceBindsModule
import com.xxmrk888ytxx.portal.di.module.SettingsScreenModule
import com.xxmrk888ytxx.portal.di.module.UnlockServiceModule
import com.xxmrk888ytxx.portal.di.scope.AppScope
import com.xxmrk888ytxx.portal.domain.AwaitUnlockRequestManager
import com.xxmrk888ytxx.portal.domain.WatchDogManager
import com.xxmrk888ytxx.portal.domain.WearDeviceSyncManager
import dagger.BindsInstance
import dagger.Component
import javax.inject.Provider

@Component(
    modules = [
        CoreModule::class,
        DataModule::class,
        DomainModule::class,
        OnboardingScreenModule::class,
        AddNewDeviceModule::class,
        MainScreenModule::class,
        DeviceConfigurationScreenModule::class,
        ComponentProvidersModule::class,
        ActivityBindsModule::class,
        ServiceBindsModule::class,
        UnlockServiceModule::class,
        BroadcastReceiverBindsModule::class,
        LogsModule::class,
        SettingsScreenModule::class
    ]
)
@AppScope
interface AppComponent {
    val activityProviderMap: Map<Class<out Activity>, @JvmSuppressWildcards Provider<Activity>>
    val serviceProviderMap: Map<Class<out Service>, @JvmSuppressWildcards Provider<Service>>
    val broadcastReceiverProviderMap: Map<Class<out BroadcastReceiver>, @JvmSuppressWildcards Provider<BroadcastReceiver>>
    val biometricEnvironmentObserver: BiometricEnvironmentObserver
    val awaitUnlockRequestManager: AwaitUnlockRequestManager
    val unsafeUnlockTypesStateObserver: UnsafeUnlockTypesStateObserver
    val watchDogManager: WatchDogManager
    val wearDeviceSyncManager: WearDeviceSyncManager

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
