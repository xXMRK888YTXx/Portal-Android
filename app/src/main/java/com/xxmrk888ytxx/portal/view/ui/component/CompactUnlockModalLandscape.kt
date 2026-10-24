package com.xxmrk888ytxx.portal.view.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.model.DeviceType
import com.xxmrk888ytxx.portal.view.model.UnlockScreenUiEvent

/**
 * Compact Bottom Sheet Unlock (Landscape).
 * Floating centered pill dialog tailored for landscape view.
 */
@Composable
fun CompactUnlockModalLandscape(
    modifier: Modifier,
    deviceName: String,
    connectionType: DeviceType?,
    fragmentActivity: FragmentActivity,
    onEvent: (UnlockScreenUiEvent) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Scrim touch consumer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
        )

        var isVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            isVisible = true
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(durationMillis = 200)),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth(0.88f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {},
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 3.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Column: Icon + Device Name + Badge
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            UnlockLockIcon(
                                containerSize = 44.dp,
                                iconSize = 22.dp,
                                cornerRadius = 14.dp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = deviceName.ifBlank { stringResource(R.string.app_name) },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                ),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            UnlockDeviceBadge(
                                connectionType = connectionType,
                                iconSize = 12.dp,
                                horizontalPadding = 10.dp,
                                verticalPadding = 4.dp,
                                textStyle = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Right Column: Title + Action Buttons
                        Column(
                            modifier = Modifier.weight(1.25f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            UnlockHeaderBadge(
                                iconSize = 12.dp,
                                horizontalPadding = 10.dp,
                                verticalPadding = 4.dp,
                                textStyle = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Primary Action: Allow
                            AllowButton(
                                fragmentActivity = fragmentActivity,
                                onEvent = onEvent,
                                modifier = Modifier.fillMaxWidth(),
                                height = 44.dp,
                                iconSize = 18.dp,
                                shapeRadius = 22.dp,
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Secondary Actions Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DenyButton(
                                    onClick = { onEvent(UnlockScreenUiEvent.Deny) },
                                    modifier = Modifier.weight(1f),
                                    height = 38.dp,
                                    iconSize = 15.dp,
                                    shapeRadius = 19.dp,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                )

                                DelayButton(
                                    onClick = { onEvent(UnlockScreenUiEvent.Delay) },
                                    modifier = Modifier.weight(1f),
                                    height = 38.dp,
                                    iconSize = 15.dp,
                                    shapeRadius = 19.dp,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
