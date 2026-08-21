package com.xxmrk888ytxx.portal.data

import android.content.Context
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.WearNodeValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WearNodeValidatorImpl @Inject constructor(
    context: Context
) : WearNodeValidator {

    private val capabilityClient = Wearable.getCapabilityClient(context)

    override suspend fun isTrustedPhoneNode(nodeId: String): Boolean = withContext(Dispatchers.IO) {
        val capabilityInfo = runCatching {
            capabilityClient.getCapability(
                WearDataLayerProtocol.CAPABILITY_PHONE_APP,
                CapabilityClient.FILTER_ALL
            ).await()
        }.getOrNull()

        val isTrusted = capabilityInfo?.nodes?.any { it.id == nodeId } == true
        fastDebugLog("Watch: Node verification for nodeId=$nodeId: isTrusted=$isTrusted (known phone nodes: ${capabilityInfo?.nodes?.map { "${it.displayName}(${it.id})" }})")
        isTrusted
    }
}
