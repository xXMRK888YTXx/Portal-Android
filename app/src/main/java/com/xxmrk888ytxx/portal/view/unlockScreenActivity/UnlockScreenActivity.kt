package com.xxmrk888ytxx.portal.view.unlockScreenActivity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.corecompose.theme.setContentWithThemeAndProviders
import com.xxmrk888ytxx.portal.view.ui.UnlockScreen
import com.xxmrk888ytxx.portal.view.unlockScreenActivity.model.UnlockScreenSideEffect
import javax.inject.Inject
import kotlin.getValue

class UnlockScreenActivity @Inject constructor(
    private val toastManager: ToastManager,
    private val unlockScreenViewModelFactory: UnlockScreenViewModel.Factory
) : FragmentActivity() {

    private val unlockScreenViewModel by viewModels<UnlockScreenViewModel> { unlockScreenViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        enableEdgeToEdge()
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

            Scaffold(Modifier.fillMaxSize()) { paddingValues ->
                val deviceName by unlockScreenViewModel.deviceName.collectAsState()
                UnlockScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                    ,
                    onEvent = unlockScreenViewModel::handleEvent,
                    deviceName = deviceName
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