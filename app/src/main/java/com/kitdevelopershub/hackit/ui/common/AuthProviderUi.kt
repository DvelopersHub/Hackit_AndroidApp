package com.kitdevelopershub.hackit.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kitdevelopershub.hackit.R
import com.kitdevelopershub.hackit.model.AuthProvider
import com.kitdevelopershub.hackit.ui.theme.HackitColors
import com.kitdevelopershub.hackit.ui.theme.ProviderColors

// iOS AuthProvider+Display.swift 相当。
// ブランドグリフは Material Icons に無いため中立な代替アイコンを使う（iOS と同方針）。

val AuthProvider.displayNameRes: Int
    get() = when (this) {
        AuthProvider.APPLE -> R.string.provider_apple
        AuthProvider.GOOGLE -> R.string.provider_google
        AuthProvider.GITHUB -> R.string.provider_github
        AuthProvider.EMAIL -> R.string.provider_email
        AuthProvider.LINE -> R.string.provider_line
        AuthProvider.X -> R.string.provider_x
        AuthProvider.SLACK -> R.string.provider_slack
        AuthProvider.FACEBOOK -> R.string.provider_facebook
        AuthProvider.DISCORD -> R.string.provider_discord
        AuthProvider.MICROSOFT -> R.string.provider_microsoft
        AuthProvider.YAHOO -> R.string.provider_yahoo
        AuthProvider.PHONE -> R.string.provider_phone
    }

val AuthProvider.icon: ImageVector
    get() = when (this) {
        AuthProvider.APPLE -> Icons.Filled.Smartphone
        AuthProvider.GOOGLE -> Icons.Filled.Language
        AuthProvider.GITHUB -> Icons.Filled.Code
        AuthProvider.EMAIL -> Icons.Filled.Email
        AuthProvider.LINE -> Icons.Filled.ChatBubble
        AuthProvider.X -> Icons.Filled.Clear
        AuthProvider.SLACK -> Icons.Filled.Tag
        AuthProvider.FACEBOOK -> Icons.Filled.ThumbUp
        AuthProvider.DISCORD -> Icons.Filled.SportsEsports
        AuthProvider.MICROSOFT -> Icons.Filled.GridView
        AuthProvider.YAHOO -> Icons.Filled.Public
        AuthProvider.PHONE -> Icons.Filled.Phone
    }

val AuthProvider.brandColor: Color
    get() = when (this) {
        AuthProvider.APPLE -> ProviderColors.Apple
        AuthProvider.GOOGLE -> ProviderColors.Google
        AuthProvider.GITHUB -> ProviderColors.GitHub
        AuthProvider.EMAIL -> HackitColors.HackitOrange
        AuthProvider.LINE -> ProviderColors.Line
        AuthProvider.X -> ProviderColors.X
        AuthProvider.SLACK -> ProviderColors.Slack
        AuthProvider.FACEBOOK -> ProviderColors.Facebook
        AuthProvider.DISCORD -> ProviderColors.Discord
        AuthProvider.MICROSOFT -> ProviderColors.Microsoft
        AuthProvider.YAHOO -> ProviderColors.Yahoo
        AuthProvider.PHONE -> ProviderColors.Phone
    }
