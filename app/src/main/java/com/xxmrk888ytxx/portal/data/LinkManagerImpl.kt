package com.xxmrk888ytxx.portal.data

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.LinkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LinkManagerImpl @Inject constructor(
    private val context: Context
) : LinkManager {
    override suspend fun openTermsOfUseLink() {
        //TODO change
        openLink("https://github.com/xXMRK888YTXx")
    }

    override suspend fun openPrivacyPolicyLink() {
        //TODO change
        openLink("https://github.com/xXMRK888YTXx")
    }

    override suspend fun openAndroidDeveloperLink() {
        openLink("https://github.com/xXMRK888YTXx")
    }

    override suspend fun openPCDeveloperLink() {
        openLink("https://github.com/KoksMen")
    }

    override suspend fun openAndroidSourceCodeLink() {
        openLink("https://github.com/xXMRK888YTXx/Portal-Android")
    }

    override suspend fun openPCSourceCodeLink() {
        openLink("https://github.com/KoksMen/PortalTest")
    }

    private suspend fun openLink(url: String) =  try {
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        withContext(Dispatchers.Main) {
            context.startActivity(browserIntent)
        }
    } catch (e: Exception) {
        fastDebugLog("Exception when try send ACTION_VIEW intent $e")
    }
}