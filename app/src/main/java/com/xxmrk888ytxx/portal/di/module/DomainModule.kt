package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.mydictionary.DI.scope.AppScope
import com.xxmrk888ytxx.portal.data.CertificateManagerImpl
import com.xxmrk888ytxx.portal.data.DeviceRepositoryImpl
import com.xxmrk888ytxx.portal.data.PreferenceManagerImpl
import com.xxmrk888ytxx.portal.data.SecureStorageImpl
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.PreferenceManager
import com.xxmrk888ytxx.portal.domain.SecureStorage
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {
    @Binds
    fun bindsPreferenceManager(
        preferenceManagerImpl: PreferenceManagerImpl
    ) : PreferenceManager

    @Binds
    fun bindsCertificateManager(
        certificateManagerImpl: CertificateManagerImpl
    ) : CertificateManager

    @Binds
    @AppScope
    fun bindsSecureStorage(
        secureStorageImpl: SecureStorageImpl
    ) : SecureStorage

    @Binds
    fun bindsDeviceRepository(
        deviceRepositoryImpl: DeviceRepositoryImpl
    ) : DeviceRepository

}