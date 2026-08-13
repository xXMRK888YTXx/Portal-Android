package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest

interface IncomingRequestPresenter {
    fun present(request: IncomingUnlockRequest)
    fun cancel(decisionId: String)
}
