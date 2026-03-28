package com.xxmrk888ytxx.portal.domain

import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.portal.domain.model.BiometricDialogEvent

interface BiometricDialogController {
    suspend fun sendRequest(
        activity: FragmentActivity,
        description: String? = null,
        onEvent: (BiometricDialogEvent) -> Unit
    )
}