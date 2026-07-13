package com.kitdevelopershub.hackit.ui.signin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kitdevelopershub.hackit.R
import com.kitdevelopershub.hackit.model.AuthProvider
import com.kitdevelopershub.hackit.ui.common.InlineError
import com.kitdevelopershub.hackit.ui.common.ProviderButton
import com.kitdevelopershub.hackit.ui.theme.HackitColors
import com.kitdevelopershub.hackit.ui.theme.Spacing

/** 前面に出す 4 プロバイダ（iOS と同一）。 */
private val PrimaryProviders = listOf(
    AuthProvider.APPLE,
    AuthProvider.GOOGLE,
    AuthProvider.GITHUB,
    AuthProvider.EMAIL,
)

/** 折りたたみ内の 8 プロバイダ（iOS と同一順）。 */
private val SecondaryProviders = listOf(
    AuthProvider.LINE,
    AuthProvider.X,
    AuthProvider.SLACK,
    AuthProvider.FACEBOOK,
    AuthProvider.DISCORD,
    AuthProvider.MICROSOFT,
    AuthProvider.YAHOO,
    AuthProvider.PHONE,
)

/**
 * サインイン画面（iOS `SignInView` 相当）。
 * メール/電話はドラフト入力画面へ遷移し、他プロバイダは即時サインイン（Mock）。
 */
@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    onNavigateToEmail: () -> Unit,
    onNavigateToPhone: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var isOtherExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HackitColors.WarmOffWhite)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.LG, vertical = Spacing.XL),
        verticalArrangement = Arrangement.spacedBy(Spacing.LG),
    ) {
        // ヘッダ
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.MD),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
                color = HackitColors.HackitOrange,
            )
            Spacer(Modifier.height(Spacing.SM))
            Text(
                stringResource(R.string.sign_in_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = HackitColors.MidGray,
            )
        }

        // 主要 4 プロバイダ
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.MD)) {
            PrimaryProviders.forEach { provider ->
                providerButton(
                    provider = provider,
                    primary = true,
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateToEmail = onNavigateToEmail,
                    onNavigateToPhone = onNavigateToPhone,
                )
            }
        }

        // 区切り
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDividerLine(Modifier.weight(1f))
            Text(
                stringResource(R.string.sign_in_or),
                style = MaterialTheme.typography.bodySmall,
                color = HackitColors.MidGray,
                modifier = Modifier.padding(horizontal = Spacing.MD),
            )
            HorizontalDividerLine(Modifier.weight(1f))
        }

        // その他 8 プロバイダ（折りたたみ）
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isOtherExpanded = !isOtherExpanded }
                .padding(vertical = Spacing.SM),
        ) {
            Text(
                stringResource(R.string.sign_in_other_methods),
                style = MaterialTheme.typography.titleMedium,
                color = HackitColors.Charcoal,
                modifier = Modifier.weight(1f),
            )
            Icon(
                if (isOtherExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = HackitColors.Charcoal,
            )
        }
        AnimatedVisibility(visible = isOtherExpanded) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.MD)) {
                SecondaryProviders.forEach { provider ->
                    providerButton(
                        provider = provider,
                        primary = false,
                        uiState = uiState,
                        viewModel = viewModel,
                        onNavigateToEmail = onNavigateToEmail,
                        onNavigateToPhone = onNavigateToPhone,
                    )
                }
            }
        }

        uiState.errorMessageRes?.let { res ->
            InlineError(message = stringResource(res), onDismiss = viewModel::dismissError)
        }
    }
}

@Composable
private fun providerButton(
    provider: AuthProvider,
    primary: Boolean,
    uiState: SignInUiState,
    viewModel: SignInViewModel,
    onNavigateToEmail: () -> Unit,
    onNavigateToPhone: () -> Unit,
) {
    val isLoading = uiState.loadingProvider == provider
    val isAnyLoading = uiState.loadingProvider != null
    ProviderButton(
        provider = provider,
        primary = primary,
        isLoading = isLoading,
        isDisabled = isAnyLoading && !isLoading,
        onClick = {
            when (provider) {
                AuthProvider.EMAIL -> onNavigateToEmail()
                AuthProvider.PHONE -> onNavigateToPhone()
                else -> viewModel.signIn(provider)
            }
        },
    )
}

@Composable
private fun HorizontalDividerLine(modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .height(1.dp)
            .background(HackitColors.LightGray),
    )
}
