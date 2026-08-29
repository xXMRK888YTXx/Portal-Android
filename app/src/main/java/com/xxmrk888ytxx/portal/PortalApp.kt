package com.xxmrk888ytxx.portal

import android.app.Application
import com.xxmrk888ytxx.coreandroid.AndroidLogger
import com.xxmrk888ytxx.portal.di.AppComponent
import com.xxmrk888ytxx.portal.di.DaggerAppComponent

class PortalApp : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.DEBUG) {
            AndroidLogger.deactivate()
        }
        appComponent.biometricEnvironmentObserver.startObserve()
        appComponent.awaitUnlockRequestManager.restoreUnlockState()
        appComponent.watchDogManager.startObserve()
        appComponent.wearDeviceSyncManager.startObserve()
        appComponent.unsafeUnlockTypesStateObserver
    }
}
