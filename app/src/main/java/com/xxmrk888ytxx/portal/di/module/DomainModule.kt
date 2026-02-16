package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.portal.data.CertificateManagerImpl
import com.xxmrk888ytxx.portal.data.PreferenceManagerImpl
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.PreferenceManager
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
}