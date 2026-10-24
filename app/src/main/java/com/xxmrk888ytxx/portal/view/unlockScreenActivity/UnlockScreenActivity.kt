package com.xxmrk888ytxx.portal.view.unlockScreenActivity

import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.corecompose.theme.setContentWithThemeAndProviders
import com.xxmrk888ytxx.portal.view.ui.UnlockScreen
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.model.UnlockScreenSideEffect
import javax.inject.Inject

open class UnlockScreenActivity @Inject constructor(
    private val toastManager: ToastManager,
    private val unlockScreenViewModelFactory: UnlockScreenViewModel.Factory
) : FragmentActivity() {

    protected val unlockScreenViewModel by viewModels<UnlockScreenViewModel> { unlockScreenViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        try {
            enableEdgeToEdge()
        } catch (_: Throwable) {}

        if (!unlockScreenViewModel.isValidIntent(intent)) {
            finish()
            return
        }
        setContentWithThemeAndProviders(
            navigator = unlockScreenViewModel,
            toastManager = toastManager,
            themeColor = unlockScreenViewModel.themeColor
        ) {
            LaunchedEffect(unlockScreenViewModel) {
                unlockScreenViewModel.effect.collect {
                    when(it) {
                        UnlockScreenSideEffect.Dismiss -> finish()
                    }
                }
            }

            val deviceName by unlockScreenViewModel.deviceName.collectAsState()
            val connectionType by unlockScreenViewModel.connectionType.collectAsState()
            val unlockScreenStyle by unlockScreenViewModel.unlockScreenStyle.collectAsState()
            val isFloating = this is FloatingUnlockScreenActivity

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = if (isFloating) Color.Transparent else MaterialTheme.colorScheme.background
            ) { paddingValues ->
                UnlockScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isFloating) androidx.compose.foundation.layout.PaddingValues(0.dp) else paddingValues),
                    onEvent = unlockScreenViewModel::handleEvent,
                    deviceName = deviceName,
                    connectionType = connectionType,
                    unlockScreenStyle = if (isFloating) unlockScreenStyle else "classic"
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        fastDebugLog("onPause")
    }

    override fun onStop() {
        super.onStop()
        unlockScreenViewModel.onStop()
        fastDebugLog("onStop")
    }

    companion object {
        const val UNLOCK_REQUEST_FROM_PC_ACTION: String = "com.xxmrk888ytxx.portal.UNLOCK_REQUEST_FROM_PC"
        const val EXTRA_UNLOCK_SCREEN_DATA = "EXTRA_UNLOCK_SCREEN_DATA"
    }
}
