package com.xxmrk888ytxx.portal.view.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.view.model.UnlockScreenUiEvent

enum class UnlockButtonSize(
    val delayHeight: Dp,
    val delayIconSize: Dp,
    val denyHeight: Dp,
    val denyIconSize: Dp,
    val allowHeight: Dp,
    val allowIconSize: Dp,
    val cornerRadius: Dp
) {
    DEFAULT(
        delayHeight = 48.dp,
        delayIconSize = 19.dp,
        denyHeight = 48.dp,
        denyIconSize = 19.dp,
        allowHeight = 54.dp,
        allowIconSize = 22.dp,
        cornerRadius = 24.dp
    ),
    COMPACT(
        delayHeight = 44.dp,
        delayIconSize = 18.dp,
        denyHeight = 44.dp,
        denyIconSize = 18.dp,
        allowHeight = 50.dp,
        allowIconSize = 20.dp,
        cornerRadius = 22.dp
    ),
    LANDSCAPE(
        delayHeight = 40.dp,
        delayIconSize = 17.dp,
        denyHeight = 40.dp,
        denyIconSize = 17.dp,
        allowHeight = 46.dp,
        allowIconSize = 19.dp,
        cornerRadius = 20.dp
    ),
    MINI(
        delayHeight = 38.dp,
        delayIconSize = 15.dp,
        denyHeight = 38.dp,
        denyIconSize = 15.dp,
        allowHeight = 44.dp,
        allowIconSize = 18.dp,
        cornerRadius = 19.dp
    )
}

@Composable
fun DelayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    iconSize: Dp = 19.dp,
    shapeRadius: Dp = 24.dp,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(shapeRadius),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.snooze),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.delay),
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DenyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    iconSize: Dp = 19.dp,
    shapeRadius: Dp = 24.dp,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(shapeRadius),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.block),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.deny),
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AllowButton(
    fragmentActivity: FragmentActivity,
    onEvent: (UnlockScreenUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 54.dp,
    iconSize: Dp = 22.dp,
    shapeRadius: Dp = 27.dp,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
) {
    Button(
        onClick = {
            onEvent(
                UnlockScreenUiEvent.Allow(
                    fragmentActivity = fragmentActivity,
                    isSentByUser = true
                )
            )
        },
        modifier = modifier.height(height),
        shape = RoundedCornerShape(shapeRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.check_circle),
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.allow_entry),
                style = textStyle
            )
        }
    }
}

@Composable
fun UnlockActionButtonsColumn(
    fragmentActivity: FragmentActivity,
    onEvent: (UnlockScreenUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    size: UnlockButtonSize = UnlockButtonSize.DEFAULT,
    spacing: Dp = 10.dp
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        DelayButton(
            onClick = { onEvent(UnlockScreenUiEvent.Delay) },
            modifier = Modifier.fillMaxWidth(),
            height = size.delayHeight,
            iconSize = size.delayIconSize,
            shapeRadius = size.cornerRadius,
            textStyle = if (size == UnlockButtonSize.LANDSCAPE || size == UnlockButtonSize.COMPACT || size == UnlockButtonSize.MINI) {
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            } else {
                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            }
        )
        DenyButton(
            onClick = { onEvent(UnlockScreenUiEvent.Deny) },
            modifier = Modifier.fillMaxWidth(),
            height = size.denyHeight,
            iconSize = size.denyIconSize,
            shapeRadius = size.cornerRadius,
            textStyle = if (size == UnlockButtonSize.LANDSCAPE || size == UnlockButtonSize.COMPACT || size == UnlockButtonSize.MINI) {
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            } else {
                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            }
        )
        AllowButton(
            fragmentActivity = fragmentActivity,
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth(),
            height = size.allowHeight,
            iconSize = size.allowIconSize,
            shapeRadius = size.cornerRadius + 3.dp,
            textStyle = if (size == UnlockButtonSize.MINI) {
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            } else {
                MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            }
        )
    }
}
