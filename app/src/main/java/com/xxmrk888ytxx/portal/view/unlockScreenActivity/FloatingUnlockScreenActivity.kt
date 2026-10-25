package com.xxmrk888ytxx.portal.view.unlockScreenActivity

import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import com.xxmrk888ytxx.coreandroid.ToastManager
import javax.inject.Inject

class FloatingUnlockScreenActivity @Inject constructor(
    toastManager: ToastManager,
    unlockScreenViewModelFactory: UnlockScreenViewModel.Factory
) : UnlockScreenActivity(toastManager, unlockScreenViewModelFactory) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = window.attributes.apply {
            dimAmount = 0.45f
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            gravity = android.view.Gravity.CENTER
        }
    }
}
