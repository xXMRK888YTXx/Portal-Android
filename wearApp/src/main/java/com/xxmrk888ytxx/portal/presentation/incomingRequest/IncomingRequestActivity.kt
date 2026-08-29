package com.xxmrk888ytxx.portal.presentation.incomingRequest

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.TimeText
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.presentation.component.WearConfirmationOverlay
import com.xxmrk888ytxx.portal.presentation.component.WearConfirmationType
import com.xxmrk888ytxx.portal.presentation.theme.PortalTheme
import javax.inject.Inject

class IncomingRequestActivity @Inject constructor(
    private val incomingRequestViewModelFactory: IncomingRequestViewModel.Factory
) : ComponentActivity() {

    private val incomingRequestViewModel by viewModels<IncomingRequestViewModel> {
        incomingRequestViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        setContent {
            val incomingRequest by incomingRequestViewModel.request.collectAsStateWithLifecycle()
            var showFailureConfirmation by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                incomingRequestViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        IncomingRequestSideEffect.NavigateBack -> finish()
                        IncomingRequestSideEffect.ShowDecisionError -> {
                            showFailureConfirmation = true
                        }
                    }
                }
            }

            PortalTheme {
                AppScaffold(
                    timeText = { TimeText() }
                ) {
                    IncomingRequestScreen(
                        request = incomingRequest,
                        onEvent = incomingRequestViewModel::handleEvent
                    )

                    WearConfirmationOverlay(
                        visible = showFailureConfirmation,
                        message = stringResource(R.string.failed_to_send_decision),
                        type = WearConfirmationType.FAILURE,
                        onDismissRequest = { showFailureConfirmation = false }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
    }

    companion object {
        const val EXTRA_DECISION_ID = "decisionId"
    }
}
