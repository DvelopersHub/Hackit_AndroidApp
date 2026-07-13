package com.kitdevelopershub.hackit.ui.event

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kitdevelopershub.hackit.R
import com.kitdevelopershub.hackit.model.Event
import com.kitdevelopershub.hackit.model.TeamMember
import com.kitdevelopershub.hackit.ui.common.AttendanceBadge
import com.kitdevelopershub.hackit.ui.common.FullScreenError
import com.kitdevelopershub.hackit.ui.common.FullScreenLoading
import com.kitdevelopershub.hackit.ui.common.LoadPhase
import com.kitdevelopershub.hackit.ui.common.circleButtonModifier
import com.kitdevelopershub.hackit.ui.theme.HackitColors
import com.kitdevelopershub.hackit.ui.theme.Radius
import com.kitdevelopershub.hackit.ui.theme.Spacing
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DateFormatter = DateTimeFormatter.ofPattern("M/d(E) HH:mm", Locale.JAPAN)

/**
 * サインイン後のメイン画面（iOS `EventInfoView` 相当）。
 * フローティングヘッダー（ハンバーガー/ロゴ/ベル）+ イベント概要 + チームメンバー一覧。
 * 表示中は 5 秒ポーリングでメンバー状態を更新する。
 */
@Composable
fun EventInfoScreen(
    viewModel: EventInfoViewModel,
    onOpenNotifications: (Event?) -> Unit,
    onOpenMentor: () -> Unit,
    onOpenSidebar: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState.phase == LoadPhase.IDLE) viewModel.load()
    }
    // 画面が表示されている間だけポーリング（iOS の .task/停止と同等のライフサイクル）
    DisposableEffect(uiState.event?.id) {
        if (uiState.event != null) viewModel.startPolling()
        onDispose { viewModel.stopPolling() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HackitColors.WarmOffWhite),
    ) {
        when (uiState.phase) {
            LoadPhase.IDLE, LoadPhase.LOADING -> FullScreenLoading()
            LoadPhase.ERROR -> FullScreenError(
                message = stringResource(R.string.event_load_failed),
                onRetry = viewModel::retry,
            )
            LoadPhase.LOADED -> LoadedContent(uiState)
        }

        FloatingHeader(
            onOpenSidebar = onOpenSidebar,
            onOpenNotifications = { onOpenNotifications(uiState.event) },
            onOpenMentor = onOpenMentor,
        )
    }
}

/** 3 層フローティングヘッダー：左ハンバーガー・中央ロゴ・右にメンター/ベル（iOS と同構成）。 */
@Composable
private fun FloatingHeader(
    onOpenSidebar: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenMentor: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = Spacing.MD, vertical = Spacing.SM),
    ) {
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            color = HackitColors.HackitOrange,
            modifier = Modifier
                .align(Alignment.Center)
                .semantics { heading() },
        )
        IconButton(
            onClick = onOpenSidebar,
            modifier = circleButtonModifier().align(Alignment.CenterStart),
        ) {
            Icon(
                Icons.Filled.Menu,
                contentDescription = stringResource(R.string.event_menu),
                tint = HackitColors.Charcoal,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            IconButton(onClick = onOpenMentor, modifier = circleButtonModifier()) {
                Icon(
                    Icons.Filled.Campaign,
                    contentDescription = stringResource(R.string.event_mentor),
                    tint = HackitColors.HackitOrange,
                )
            }
            IconButton(onClick = onOpenNotifications, modifier = circleButtonModifier()) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = stringResource(R.string.event_notifications),
                    tint = HackitColors.HackitOrange,
                )
            }
        }
    }
}

@Composable
private fun LoadedContent(uiState: EventInfoUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.LG)
            .padding(top = 88.dp, bottom = Spacing.XL),
        verticalArrangement = Arrangement.spacedBy(Spacing.LG),
    ) {
        uiState.event?.let { EventSummaryCard(event = it, uiState = uiState) }
        TeamMembersSection(members = uiState.teamMembers)
    }
}

/** イベント名・日時・会場・自分の受付状態（要件: iter-001 の Hackit 情報画面）。 */
@Composable
private fun EventSummaryCard(event: Event, uiState: EventInfoUiState) {
    Card {
        Column(
            modifier = Modifier.padding(Spacing.MD),
            verticalArrangement = Arrangement.spacedBy(Spacing.SM),
        ) {
            Text(
                event.name,
                style = MaterialTheme.typography.headlineSmall,
                color = HackitColors.Charcoal,
            )
            LabeledRow(
                label = stringResource(R.string.event_schedule_label),
                value = stringResource(
                    R.string.event_date_range,
                    DateFormatter.withZone(ZoneId.systemDefault()).format(event.startsAt),
                    DateFormatter.withZone(ZoneId.systemDefault()).format(event.endsAt),
                ),
            )
            LabeledRow(label = stringResource(R.string.event_venue_label), value = event.venueName)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.event_own_status_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = HackitColors.MidGray,
                    modifier = Modifier.weight(1f),
                )
                AttendanceBadge(status = uiState.ownStatus)
            }
        }
    }
}

@Composable
private fun LabeledRow(label: String, value: String) {
    Row {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = HackitColors.MidGray,
            modifier = Modifier.width(48.dp),
        )
        Spacer(Modifier.width(Spacing.SM))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = HackitColors.Charcoal)
    }
}

@Composable
private fun TeamMembersSection(members: List<TeamMember>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
        Text(
            stringResource(R.string.event_team_members),
            style = MaterialTheme.typography.headlineSmall,
            color = HackitColors.Charcoal,
            modifier = Modifier.semantics { heading() },
        )
        if (members.isEmpty()) {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.MD),
                    verticalArrangement = Arrangement.spacedBy(Spacing.SM),
                ) {
                    Text(
                        stringResource(R.string.event_team_members_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = HackitColors.Charcoal,
                    )
                    Text(
                        stringResource(R.string.event_team_members_empty_caption),
                        style = MaterialTheme.typography.bodySmall,
                        color = HackitColors.MidGray,
                    )
                }
            }
        } else {
            Card {
                Column {
                    members.forEachIndexed { index, member ->
                        MemberRow(member)
                        if (index < members.lastIndex) {
                            HorizontalDivider(
                                color = HackitColors.LightGray,
                                modifier = Modifier.padding(start = Spacing.MD),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberRow(member: TeamMember) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.MD, vertical = 12.dp),
    ) {
        Icon(
            if (member.isLeader) Icons.Filled.Star else Icons.Filled.Person,
            contentDescription = null,
            tint = if (member.isLeader) HackitColors.HighlightYellow else HackitColors.MidGray,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(Spacing.MD))
        Text(
            member.displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = HackitColors.Charcoal,
            modifier = Modifier.weight(1f),
        )
        AttendanceBadge(status = member.status)
    }
}

/** 白背景+薄枠の角丸カード（iOS の RoundedRectangle + stroke 相当）。 */
@Composable
private fun Card(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(Radius.LG),
        color = androidx.compose.ui.graphics.Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, HackitColors.LightGray),
        modifier = Modifier.fillMaxWidth(),
    ) {
        content()
    }
}
