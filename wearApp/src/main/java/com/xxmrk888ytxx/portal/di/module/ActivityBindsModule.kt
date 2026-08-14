package com.xxmrk888ytxx.portal.di.module

import android.app.Activity
import com.xxmrk888ytxx.portal.di.key.ActivityKey
import com.xxmrk888ytxx.portal.presentation.mainActivity.MainActivity
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Multibindings used by [com.xxmrk888ytxx.portal.PortalComponentFactory] to create activities.
 */
@Module
interface ActivityBindsModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    fun bindMainActivityToActivityForMultiBinding(activity: MainActivity): Activity
}
