package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.portal.data.IncomingRequestPresenterImpl
import com.xxmrk888ytxx.portal.data.IncomingRequestRepositoryImpl
import com.xxmrk888ytxx.portal.data.WearPermissionCheckerImpl
import com.xxmrk888ytxx.portal.data.WearPhoneGatewayImpl
import com.xxmrk888ytxx.portal.data.WearProfileRepositoryImpl
import com.xxmrk888ytxx.portal.data.WearSettingsRepositoryImpl
import com.xxmrk888ytxx.portal.di.scope.AppScope
import com.xxmrk888ytxx.portal.domain.IncomingRequestPresenter
import com.xxmrk888ytxx.portal.domain.IncomingRequestRepository
import com.xxmrk888ytxx.portal.domain.WearPermissionChecker
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import com.xxmrk888ytxx.portal.domain.WearProfileRepository
import com.xxmrk888ytxx.portal.domain.WearSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Module
interface DataModule {

    @Binds
    @AppScope
    fun bindsWearProfileRepository(
        wearProfileRepositoryImpl: WearProfileRepositoryImpl
    ): WearProfileRepository

    @Binds
    @AppScope
    fun bindsIncomingRequestRepository(
        incomingRequestRepositoryImpl: IncomingRequestRepositoryImpl
    ): IncomingRequestRepository

    @Binds
    @AppScope
    fun bindsWearSettingsRepository(
        wearSettingsRepositoryImpl: WearSettingsRepositoryImpl
    ): WearSettingsRepository

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
        @Provides
        @AppScope
        fun providesJson(): Json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }
}
