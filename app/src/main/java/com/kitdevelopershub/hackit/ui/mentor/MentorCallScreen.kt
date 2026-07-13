package com.kitdevelopershub.hackit.ui.mentor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.kitdevelopershub.hackit.model.MentorCall
import com.kitdevelopershub.hackit.model.TechArea
import com.kitdevelopershub.hackit.ui.common.FullScreenError
import com.kitdevelopershub.hackit.ui.common.FullScreenLoading
import com.kitdevelopershub.hackit.ui.common.LoadPhase
import com.kitdevelopershub.hackit.ui.common.circleButtonModifier
import com.kitdevelopershub.hackit.ui.theme.HackitColors
import com.kitdevelopershub.hackit.ui.theme.Radius
import com.kitdevelopershub.hackit.ui.theme.Spacing

/**
 * メンター呼び出し画面（iOS `MentorCallView` 相当）。
 * 進行中の呼び出しが無ければ入力フォーム、あれば状態カード+取消を出す。
 */
@Composable
fun MentorCallScreen(viewModel: MentorCallViewModel, onBack: () -> Unit) {
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
                message = stringResource(R.string.mentor_load_failed),
                onRetry = viewModel::load,
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
            stringResource(R.string.mentor_title),
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
private fun LoadedContent(uiState: MentorCallUiState, viewModel: MentorCallViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.LG)
            .padding(top = 88.dp, bottom = Spacing.XL),
        verticalArrangement = Arrangement.spacedBy(Spacing.LG),
    ) {
        val active = uiState.activeCall
        if (active != null) {
            ActiveCallCard(call = active, isSubmitting = uiState.isSubmitting, onCancel = viewModel::cancelCall)
        } else {
            RequestForm(uiState, viewModel)
        }
        if (uiState.errorMessage) {
            Text(
                stringResource(R.string.mentor_request_failed),
                style = MaterialTheme.typography.bodySmall,
                color = HackitColors.StatusError,
            )
        }
    }
}

/** 待ち順つきの状態カード。queuePosition==1 のときは「次はあなたの番です」を出す。 */
@Composable
private fun ActiveCallCard(call: MentorCall, isSubmitting: Boolean, onCancel: () -> Unit) {
    Card {
        Column(
            modifier = Modifier.padding(Spacing.MD),
            verticalArrangement = Arrangement.spacedBy(Spacing.SM),
        ) {
            Text(
                stringResource(R.string.mentor_active_header),
                style = MaterialTheme.typography.titleMedium,
                color = HackitColors.Charcoal,
            )
            val queueText = if (call.queuePosition <= 1) {
                stringResource(R.string.mentor_queue_your_turn)
            } else {
                stringResource(R.string.mentor_queue_waiting, call.queuePosition)
            }
            Text(queueText, style = MaterialTheme.typography.bodyLarge, color = HackitColors.HackitOrange)
            LabeledRow(stringResource(R.string.mentor_area_label), stringResource(call.techArea.displayNameRes))
            LabeledRow(stringResource(R.string.mentor_table_label), call.tableNumber)
            if (call.message.isNotBlank()) {
                LabeledRow(stringResource(R.string.mentor_message_label), call.message)
            }
            OutlinedButton(
                onClick = onCancel,
                enabled = !isSubmitting,
                shape = RoundedCornerShape(Radius.MD),
                border = BorderStroke(1.dp, HackitColors.StatusError),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 50.dp),
            ) {
                Text(stringResource(R.string.mentor_cancel_button), color = HackitColors.StatusError)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun RequestForm(uiState: MentorCallUiState, viewModel: MentorCallViewModel) {
    Card {
        Column(
            modifier = Modifier.padding(Spacing.MD),
            verticalArrangement = Arrangement.spacedBy(Spacing.MD),
        ) {
            Text(
                stringResource(R.string.mentor_area_label),
                style = MaterialTheme.typography.labelMedium,
                color = HackitColors.MidGray,
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                TechArea.entries.forEach { area ->
                    FilterChip(
                        selected = uiState.selectedTechArea == area,
                        onClick = { viewModel.setTechArea(area) },
                        label = { Text(stringResource(area.displayNameRes)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HackitColors.HackitOrange,
                            selectedLabelColor = Color.White,
                        ),
                    )
                }
            }
            MentorTextField(
                value = uiState.tableNumber,
                onValueChange = viewModel::setTableNumber,
                placeholderRes = R.string.mentor_table_placeholder,
            )
            MentorTextField(
                value = uiState.message,
                onValueChange = viewModel::setMessage,
                placeholderRes = R.string.mentor_message_placeholder,
            )
            Button(
                onClick = viewModel::requestCall,
                enabled = !uiState.isSubmitting,
                shape = RoundedCornerShape(Radius.MD),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HackitColors.HackitOrange,
                    disabledContainerColor = HackitColors.LightGray,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 50.dp),
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White,
                    )
                    Spacer(Modifier.width(Spacing.SM))
                }
                Icon(Icons.Filled.Campaign, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(Spacing.SM))
                Text(stringResource(R.string.mentor_request_button), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MentorTextField(value: String, onValueChange: (String) -> Unit, placeholderRes: Int) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(placeholderRes), color = HackitColors.MidGray) },
        singleLine = true,
        shape = RoundedCornerShape(Radius.MD),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = HackitColors.HackitOrange,
            unfocusedBorderColor = HackitColors.LightGray,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun LabeledRow(label: String, value: String) {
    Row {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = HackitColors.MidGray,
            modifier = Modifier.width(72.dp),
        )
        Spacer(Modifier.width(Spacing.SM))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = HackitColors.Charcoal)
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
