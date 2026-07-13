package com.kitdevelopershub.hackit.ui.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kitdevelopershub.hackit.R
import com.kitdevelopershub.hackit.model.AuthProvider
import com.kitdevelopershub.hackit.ui.common.InlineError
import com.kitdevelopershub.hackit.ui.theme.HackitColors
import com.kitdevelopershub.hackit.ui.theme.Radius
import com.kitdevelopershub.hackit.ui.theme.Spacing

/** メール入力（iOS `EmailEntryView` 相当）。空チェックのみで Mock に委ねる。 */
@Composable
fun EmailEntryScreen(viewModel: SignInViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    EntryScaffold(
        screenTitleRes = R.string.email_entry_screen_title,
        titleRes = R.string.email_entry_title,
        captionRes = R.string.email_entry_caption,
        placeholderRes = R.string.email_entry_placeholder,
        value = uiState.emailDraft,
        onValueChange = viewModel::updateEmailDraft,
        keyboardType = KeyboardType.Email,
        isLoading = uiState.loadingProvider == AuthProvider.EMAIL,
        isAnyLoading = uiState.loadingProvider != null,
        errorMessageRes = uiState.errorMessageRes,
        onSubmit = viewModel::submitEmail,
        onDismissError = viewModel::dismissError,
        onBack = onBack,
    )
}

/** 電話番号入力（iOS `PhoneEntryView` 相当）。数字のみ受け付ける。 */
@Composable
fun PhoneEntryScreen(viewModel: SignInViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    EntryScaffold(
        screenTitleRes = R.string.phone_entry_screen_title,
        titleRes = R.string.phone_entry_title,
        captionRes = R.string.phone_entry_caption,
        placeholderRes = R.string.phone_entry_placeholder,
        value = uiState.phoneDraft,
        onValueChange = viewModel::updatePhoneDraft,
        keyboardType = KeyboardType.Number,
        isLoading = uiState.loadingProvider == AuthProvider.PHONE,
        isAnyLoading = uiState.loadingProvider != null,
        errorMessageRes = uiState.errorMessageRes,
        onSubmit = viewModel::submitPhone,
        onDismissError = viewModel::dismissError,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryScaffold(
    screenTitleRes: Int,
    titleRes: Int,
    captionRes: Int,
    placeholderRes: Int,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    isLoading: Boolean,
    isAnyLoading: Boolean,
    errorMessageRes: Int?,
    onSubmit: () -> Unit,
    onDismissError: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(screenTitleRes), style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = HackitColors.Charcoal,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HackitColors.WarmOffWhite),
            )
        },
        containerColor = HackitColors.WarmOffWhite,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.LG, vertical = Spacing.LG),
            verticalArrangement = Arrangement.spacedBy(Spacing.LG),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS)) {
                Text(
                    stringResource(titleRes),
                    style = MaterialTheme.typography.headlineSmall,
                    color = HackitColors.Charcoal,
                )
                Text(
                    stringResource(captionRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = HackitColors.MidGray,
                )
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(stringResource(placeholderRes), color = HackitColors.MidGray) },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true,
                shape = RoundedCornerShape(Radius.MD),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                    focusedBorderColor = HackitColors.HackitOrange,
                    unfocusedBorderColor = HackitColors.LightGray,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = onSubmit,
                enabled = !isAnyLoading,
                shape = RoundedCornerShape(Radius.MD),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HackitColors.HackitOrange,
                    disabledContainerColor = HackitColors.LightGray,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 50.dp),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = androidx.compose.ui.graphics.Color.White,
                    )
                    Spacer(Modifier.size(Spacing.SM))
                }
                Text(stringResource(R.string.continue_button), style = MaterialTheme.typography.labelLarge)
            }

            errorMessageRes?.let { res ->
                InlineError(message = stringResource(res), onDismiss = onDismissError)
            }

            Spacer(Modifier.height(Spacing.XL))
        }
    }
}
