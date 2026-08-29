package com.xxmrk888ytxx.portal.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.xxmrk888ytxx.portal.R
import kotlinx.coroutines.delay

enum class WearConfirmationType {
    SUCCESS,
    FAILURE
}

/**
 * Standard, beautiful Wear OS confirmation overlay that displays centered on round screens
 * with animated check/close hero icons and auto-dismiss.
 */
@Composable
fun WearConfirmationOverlay(
    visible: Boolean,
    message: String,
    type: WearConfirmationType = WearConfirmationType.SUCCESS,
    durationMillis: Long = 2500L,
    onDismissRequest: () -> Unit
) {
    if (visible) {
        LaunchedEffect(message, type) {
            delay(durationMillis)
            onDismissRequest()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.85f),
        exit = fadeOut() + scaleOut(targetScale = 0.85f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            when (type) {
                                WearConfirmationType.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
                                WearConfirmationType.FAILURE -> MaterialTheme.colorScheme.errorContainer
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            when (type) {
                                WearConfirmationType.SUCCESS -> R.drawable.check
                                WearConfirmationType.FAILURE -> R.drawable.close
                            }
                        ),
                        contentDescription = null,
                        tint = when (type) {
                            WearConfirmationType.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
                            WearConfirmationType.FAILURE -> MaterialTheme.colorScheme.onErrorContainer
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
