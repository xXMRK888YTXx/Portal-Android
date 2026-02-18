package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.Certificate
import com.xxmrk888ytxx.portal.domain.model.PairResult

interface PortalApi {
    suspend fun pair(host: String, pairCode: String, certificate: Certificate): Result<PairResult>
}