package com.kitdevelopershub.hackit.ui.notifications

import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kitdevelopershub.hackit.R
import com.kitdevelopershub.hackit.model.AppNotification
import com.kitdevelopershub.hackit.ui.common.FullScreenError
import com.kitdevelopershub.hackit.ui.common.FullScreenLoading
import com.kitdevelopershub.hackit.ui.common.LoadPhase
import com.kitdevelopershub.hackit.ui.common.circleButtonModifier
import com.kitdevelopershub.hackit.ui.theme.HackitColors
import com.kitdevelopershub.hackit.ui.theme.Radius
import com.kitdevelopershub.hackit.ui.theme.Spacing

/**
 * 通知タイムライン画面（iOS `NotificationView` 相当）。
 * イベント情報から遷移した場合のみ、フェイルセーフの手動受付セクションを上部に出す。
 */
@Composable
fun NotificationScreen(viewModel: NotificationViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState.phase == LoadPhase.IDLE) viewModel.load()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HackitColors.WarmOffWhite),
    ) {
        when (uiState.phase) {
            LoadPhase.IDLE, LoadPhase.LOADING -> FullScreenLoading()
            LoadPhase.ERROR -> FullScreenError(
                message = stringResource(R.string.notification_load_failed),
                onRetry = viewModel::retry,
            )
            LoadPhase.LOADED -> LoadedContent(uiState, viewModel)
        }

        FloatingHeader(onBack = onBack)
    }
}

@Composable
private fun FloatingHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = Spacing.MD, vertical = Spacing.SM),
    ) {
        Text(
            stringResource(R.string.notification_title),
            style = MaterialTheme.typography.headlineSmall,
            color = HackitColors.Charcoal,
            modifier = Modifier
                .align(Alignment.Center)
                .semantics { heading() },
        )
        IconButton(
            onClick = onBack,
            modifier = circleButtonModifier().align(Alignment.CenterStart),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = HackitColors.Charcoal,
            )
        }
    }
}

@Composable
private fun LoadedContent(uiState: NotificationUiState, viewModel: NotificationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.LG)
            .padding(top = 88.dp, bottom = Spacing.XL),
        verticalArrangement = Arrangement.spacedBy(Spacing.MD),
    ) {
        if (viewModel.canManualCheckIn) {
            ManualCheckInSection(uiState, viewModel)
        }
        if (uiState.notifications.isEmpty()) {
            EmptyView()
        } else {
            NotificationList(uiState.notifications)
        }
    }
}

/** 「自動受付できないとき」のフェイルセーフ手動受付（iOS と同一文言・同一状態遷移）。 */
@Composable
private fun ManualCheckInSection(uiState: NotificationUiState, viewModel: NotificationViewModel) {
    Card {
        Column(
            modifier = Modifier.padding(Spacing.MD),
            verticalArrangement = Arrangement.spacedBy(Spacing.SM),
        ) {
            Text(
                stringResource(R.string.manual_check_in_header),
                style = MaterialTheme.typography.labelMedium,
                color = HackitColors.MidGray,
            )
            Text(
                stringResource(R.string.manual_check_in_caption),
                style = MaterialTheme.typography.bodySmall,
                color = HackitColors.MidGray,
            )
            OutlinedButton(
                onClick = viewModel::manualCheckIn,
                enabled = !uiState.isManualCheckingIn && !uiState.didManualCheckIn,
                shape = RoundedCornerShape(Radius.MD),
                border = BorderStroke(1.dp, HackitColors.HackitOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 44.dp),
            ) {
                when {
                    uiState.isManualCheckingIn -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = HackitColors.HackitOrange,
                        )
                    }
                    uiState.didManualCheckIn -> {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = HackitColors.HackitOrange,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(Spacing.SM))
                        Text(
                            stringResource(R.string.manual_check_in_done),
                            style = MaterialTheme.typography.labelLarge,
                            color = HackitColors.HackitOrange,
                        )
                    }
                    else -> {
                        Icon(
                            Icons.Filled.TouchApp,
                            contentDescription = null,
                            tint = HackitColors.HackitOrange,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(Spacing.SM))
                        Text(
                            stringResource(R.string.manual_check_in_button),
                            style = MaterialTheme.typography.labelLarge,
                            color = HackitColors.HackitOrange,
                        )
                    }
                }
            }
            if (uiState.manualCheckInError) {
                Text(
                    stringResource(R.string.manual_check_in_failed),
                    style = MaterialTheme.typography.bodySmall,
                    color = HackitColors.StatusError,
                )
            }
        }
    }
}

@Composable
private fun NotificationList(notifications: List<AppNotification>) {
    Card {
        Column {
            notifications.forEachIndexed { index, item ->
                NotificationRow(item)
                if (index < notifications.lastIndex) {
                    HorizontalDivider(
                        color = HackitColors.LightGray,
                        modifier = Modifier.padding(start = Spacing.LG),
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(item: AppNotification) {
    val (icon, tint) = when (item.kind) {
        AppNotification.Kind.CHECK_IN_SUCCESS -> Icons.Filled.CheckCircle to HackitColors.StatusSuccess
        AppNotification.Kind.CHECK_OUT -> Icons.Filled.Cancel to HackitColors.MidGray
        AppNotification.Kind.REMINDER -> Icons.Filled.Notifications to HackitColors.HackitOrange
    }
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.MD),
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(Spacing.MD))
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.XS),
            modifier = Modifier.weight(1f),
        ) {
            Text(item.title, style = MaterialTheme.typography.titleMedium, color = HackitColors.Charcoal)
            Text(item.body, style = MaterialTheme.typography.bodySmall, color = HackitColors.MidGray)
        }
        Spacer(Modifier.width(Spacing.SM))
        Text(
            DateUtils.getRelativeTimeSpanString(
                item.timestamp.toEpochMilli(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE,
            ).toString(),
            style = MaterialTheme.typography.labelMedium,
            color = HackitColors.MidGray,
        )
    }
}

@Composable
private fun EmptyView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Filled.NotificationsOff,
            contentDescription = null,
            tint = HackitColors.MidGray,
            modifier = Modifier.size(36.dp),
        )
        Spacer(Modifier.height(Spacing.MD))
        Text(
            stringResource(R.string.notification_empty),
            style = MaterialTheme.typography.bodySmall,
            color = HackitColors.MidGray,
        )
    }
}

@Composable
private fun Card(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(Radius.LG),
        color = Color.White,
        border = BorderStroke(1.dp, HackitColors.LightGray),
        modifier = Modifier.fillMaxWidth(),
    ) {
        content()
    }
}
