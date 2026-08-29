package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest

/**
 * Presents and dismisses incoming unlock request notifications on the watch.
 *
 * Wear OS background delivery is notification-only in this app. The notification opens the same
 * request screen that is used when the app is already visible.
 */
interface IncomingRequestPresenter {
    fun present(request: IncomingUnlockRequest)
    fun cancel(decisionId: String)
}
