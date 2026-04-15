package com.xxmrk888ytxx.portal.providedContract.settingsScreen

import com.xxmrk888ytxx.portal.domain.LinkManager
import com.xxmrk888ytxx.settingsscreen.contract.OpenLinkContract
import javax.inject.Inject

class OpenLinkContractImpl @Inject constructor(
    private val linkManager: LinkManager
) : OpenLinkContract {
    override suspend fun openTermsOfUseLink() = linkManager.openTermsOfUseLink()

    override suspend fun openPrivacyPolicyLink() = linkManager.openPrivacyPolicyLink()

    override suspend fun openPCSourceCodeLink() = linkManager.openPCSourceCodeLink()

    override suspend fun openAndroidDeveloperLink() = linkManager.openAndroidDeveloperLink()

    override suspend fun openPCDeveloperLink() = linkManager.openPCDeveloperLink()

    override suspend fun openAndroidSourceCodeLink() = linkManager.openAndroidSourceCodeLink()
    override suspend fun openPCClientDownloadLink() = linkManager.openPCClientDownloadLink()
}