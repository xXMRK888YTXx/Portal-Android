package com.xxmrk888ytxx.portal.di.module

import android.content.Context
import com.xxmrk888ytxx.portal.data.DeviceRepositoryImpl
import com.xxmrk888ytxx.portal.data.IncomingRequestPresenterImpl
import com.xxmrk888ytxx.portal.data.IncomingRequestRepositoryImpl
import com.xxmrk888ytxx.portal.data.WearPermissionCheckerImpl
import com.xxmrk888ytxx.portal.data.WearPhoneGatewayImpl
import com.xxmrk888ytxx.portal.di.scope.AppScope
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.IncomingRequestPresenter
import com.xxmrk888ytxx.portal.domain.IncomingRequestRepository
import com.xxmrk888ytxx.portal.domain.WearPermissionChecker
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json

@Module
interface DataModule {

    @Binds
    @AppScope
    fun bindsDeviceRepository(
        deviceRepositoryImpl: DeviceRepositoryImpl
    ): DeviceRepository

    @Binds
    @AppScope
    fun bindsIncomingRequestRepository(
        incomingRequestRepositoryImpl: IncomingRequestRepositoryImpl
    ): IncomingRequestRepository

    @Binds
    fun bindsWearPermissionChecker(
        wearPermissionCheckerImpl: WearPermissionCheckerImpl
    ): WearPermissionChecker

    @Binds
    fun bindsWearPhoneGateway(
        wearPhoneGatewayImpl: WearPhoneGatewayImpl
    ): WearPhoneGateway

    @Binds
    fun bindsIncomingRequestPresenter(
        incomingRequestPresenterImpl: IncomingRequestPresenterImpl
    ): IncomingRequestPresenter

    companion object {
        private const val PREFERENCES_NAME_FILE = "wear_preferences_storage"

        @Provides
        @AppScope
        fun providesJson(): Json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }

        @Provides
        @AppScope
        fun providesPreferencesStorage(context: Context): PreferencesStorage =
            PreferencesStorage.Factory().create(PREFERENCES_NAME_FILE, context)

        @Provides
        @AppScope
        fun providesApplicationScope(): CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
