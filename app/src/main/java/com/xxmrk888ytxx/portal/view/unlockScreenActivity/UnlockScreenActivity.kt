package com.xxmrk888ytxx.portal.view.unlockScreenActivity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.biometricauthentication.compose.rememberBiometricAuthManager
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.corecompose.theme.setContentWithThemeAndProviders
import com.xxmrk888ytxx.portal.view.mainActivity.ActivityViewModel
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
        setContent {
            setContentWithThemeAndProviders(
                navigator = unlockScreenViewModel,
                toastManager = toastManager
            ) {
                LaunchedEffect(unlockScreenViewModel) {
                    unlockScreenViewModel.effect.collect {
                        when(it) {
                            UnlockScreenSideEffect.Dismiss -> finish()
                        }
                    }
                }

                Scaffold(Modifier.fillMaxSize()) {

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        unlockScreenViewModel.requestBiometricAuth(this@UnlockScreenActivity)
    }

    companion object {
        const val EXTRA_UNLOCK_SCREEN_DATA = "EXTRA_UNLOCK_SCREEN_DATA"
    }
}