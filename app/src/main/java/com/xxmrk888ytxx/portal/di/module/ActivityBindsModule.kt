package com.xxmrk888ytxx.portal.di.module

import android.app.Activity
import com.xxmrk888ytxx.portal.di.key.ActivityKey
import com.xxmrk888ytxx.portal.view.shortcutUnlockActivity.ShortcutUnlockActivity
import com.xxmrk888ytxx.portal.view.mainActivity.MainActivity
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.UnlockScreenActivity
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ActivityBindsModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    fun bindMainActivityToActivityForMultiBinding(activity: MainActivity): Activity

    @Binds
    @IntoMap
    @ActivityKey(UnlockScreenActivity::class)
    fun bindUnlockScreenActivityToActivityForMultiBinding(activity: UnlockScreenActivity): Activity

    @Binds
    @IntoMap
    @ActivityKey(ShortcutUnlockActivity::class)
    fun bindFastUnlockActivityToActivityForMultiBinding(activity: ShortcutUnlockActivity): Activity
}