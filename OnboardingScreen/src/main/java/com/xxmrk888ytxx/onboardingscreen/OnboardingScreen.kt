package com.xxmrk888ytxx.onboardingscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.xxmrk888ytxx.coreandroid.mvi.UiEvent
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.corecompose.LocalNavigator
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenSideEffect
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenUiEvent
import com.xxmrk888ytxx.onboardingscreen.model.ScreenState
import kotlinx.coroutines.flow.Flow

@Composable
fun OnboardingScreen(
    state: ScreenState,
    onEvent: (OnboardingScreenUiEvent) -> Unit,
    sideEffect: Flow<OnboardingScreenSideEffect>
) {

    val navigator = LocalNavigator.current
    HandleSideEffect(sideEffect) { effect ->
        when(effect) {
            OnboardingScreenSideEffect.FinishOnboarding -> navigator.fromOnboardingScreenToMainScreen()
        }
    }

    Column(Modifier.fillMaxSize()) {
        Text("OnboardingScreen")
        Button(onClick = { onEvent(OnboardingScreenUiEvent.NextPage) }) {
            Text("Next")
        }
    }

}