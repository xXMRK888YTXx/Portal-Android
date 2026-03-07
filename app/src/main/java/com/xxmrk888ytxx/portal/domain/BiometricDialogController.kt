package com.xxmrk888ytxx.portal.domain

import androidx.fragment.app.FragmentActivity

interface BiometricDialogController {
    suspend fun sendRequest(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    )
}