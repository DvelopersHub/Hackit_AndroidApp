package com.kitdevelopershub.hackit.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kitdevelopershub.hackit.R
import com.kitdevelopershub.hackit.model.AttendanceStatus
import com.kitdevelopershub.hackit.model.AuthProvider
import com.kitdevelopershub.hackit.ui.theme.HackitColors
import com.kitdevelopershub.hackit.ui.theme.Radius
import com.kitdevelopershub.hackit.ui.theme.Spacing

/** プロバイダ共通ボタン（iOS `ProviderButton` 相当）。primary=塗り、secondary=枠線。 */
@Composable
fun ProviderButton(
    provider: AuthProvider,
    primary: Boolean,
    isLoading: Boolean,
    isDisabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(R.string.sign_in_provider_button, stringResource(provider.displayNameRes))
    val shape = RoundedCornerShape(Radius.MD)
    // Button / OutlinedButton の RowScope を受け取る（Modifier.weight を使うため）
    val content: @Composable RowScope.() -> Unit = {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = if (primary) Color.White else HackitColors.HackitOrange,
            )
        } else {
            Icon(imageVector = provider.icon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(Spacing.SM))
        Text(label, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.weight(1f))
    }

    if (primary) {
        Button(
            onClick = onClick,
            enabled = !isDisabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = provider.brandColor,
                contentColor = Color.White,
                disabledContainerColor = HackitColors.LightGray,
                disabledContentColor = HackitColors.MidGray,
            ),
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 50.dp),
        ) { content() }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = !isDisabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = HackitColors.Charcoal,
                disabledContentColor = HackitColors.MidGray,
            ),
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 50.dp),
        ) { content() }
    }
}

/** 受付状態バッジ（iOS `AttendanceBadge` 相当）。色+アイコンの併用で色覚多様性に配慮。 */
@Composable
fun AttendanceBadge(status: AttendanceStatus, modifier: Modifier = Modifier) {
    val (icon, labelRes) = when (status) {
        AttendanceStatus.NOT_ARRIVED -> Icons.Filled.RadioButtonUnchecked to R.string.attendance_not_arrived
        AttendanceStatus.PRESENT -> Icons.Filled.CheckCircle to R.string.attendance_present
        AttendanceStatus.EXITED -> Icons.Filled.Cancel to R.string.attendance_exited
    }
    val foreground = when (status) {
        AttendanceStatus.NOT_ARRIVED -> HackitColors.MidGray
        else -> Color.White
    }
    val background = when (status) {
        AttendanceStatus.NOT_ARRIVED -> HackitColors.LightGray
        AttendanceStatus.PRESENT -> HackitColors.StatusSuccess
        AttendanceStatus.EXITED -> HackitColors.MidGray
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(background, CircleShape)
            .padding(horizontal = Spacing.SM, vertical = Spacing.XS),
    ) {
        Icon(icon, contentDescription = null, tint = foreground, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(Spacing.XS))
        Text(
            stringResource(labelRes),
            style = MaterialTheme.typography.labelMedium,
            color = foreground,
        )
    }
}

/** サインイン画面下部のインラインエラー（iOS `InlineErrorView` 相当）。 */
@Composable
fun InlineError(message: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .background(
                HackitColors.StatusWarning.copy(alpha = 0.12f),
                RoundedCornerShape(Radius.MD),
            )
            .padding(Spacing.SM),
    ) {
        Icon(
            Icons.Filled.Warning,
            contentDescription = null,
            tint = HackitColors.StatusWarning,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(Spacing.SM))
        Text(
            message,
            style = MaterialTheme.typography.bodySmall,
            color = HackitColors.Charcoal,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.dismiss), color = HackitColors.Charcoal)
        }
    }
}

/** ロード中のフルスクリーン表示。 */
@Composable
fun FullScreenLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = HackitColors.HackitOrange)
        Spacer(Modifier.size(Spacing.MD))
        Text(
            stringResource(R.string.loading),
            style = MaterialTheme.typography.bodyMedium,
            color = HackitColors.MidGray,
        )
    }
}

/** エラー+再試行のフルスクリーン表示（iOS の errorView 相当）。 */
@Composable
fun FullScreenError(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.XL),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Filled.Warning,
            contentDescription = null,
            tint = HackitColors.StatusWarning,
            modifier = Modifier.size(40.dp),
        )
        Spacer(Modifier.size(Spacing.MD))
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = HackitColors.MidGray,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(Spacing.MD))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(Radius.MD),
            colors = ButtonDefaults.buttonColors(containerColor = HackitColors.HackitOrange),
            modifier = Modifier.defaultMinSize(minWidth = 120.dp, minHeight = 50.dp),
        ) {
            Text(stringResource(R.string.retry), style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** フローティングヘッダー用の丸形アイコンボタン背景。 */
@Composable
fun circleButtonModifier(): Modifier = Modifier
    .size(48.dp)
    .background(Color.White.copy(alpha = 0.85f), CircleShape)
    .border(1.dp, HackitColors.LightGray, CircleShape)
