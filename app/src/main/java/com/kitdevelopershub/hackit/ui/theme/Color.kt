package com.kitdevelopershub.hackit.ui.theme

import androidx.compose.ui.graphics.Color

// Hackit DesignSystem ブランドカラー（iOS Theme/Colors.swift と同値）。
// MVP はライトモードのみ（iOS と同方針）。

object HackitColors {
    // Brand
    val HackitOrange = Color(0xFFFF6B35)
    val HighlightYellow = Color(0xFFFFD93D)

    // Neutral
    val WarmOffWhite = Color(0xFFFFFBF7)
    val Charcoal = Color(0xFF2D2D2D)
    val MidGray = Color(0xFF6B6B6B)
    val LightGray = Color(0xFFE5E5E5)

    // Status
    val StatusSuccess = Color(0xFF34C759)
    val StatusWarning = Color(0xFFFF9500)
    val StatusError = Color(0xFFFF3B30)
    val StatusInfo = Color(0xFF6BCBFF)
}

/** プロバイダボタンのブランド色（iOS AuthProvider+Display.swift と同値）。 */
object ProviderColors {
    val Apple = Color(0xFF000000)
    val Google = Color(0xFFF24236)
    val GitHub = Color(0xFF212933)
    val Line = Color(0xFF00B84F)
    val X = Color(0xFF000000)
    val Slack = Color(0xFF4A0D5C)
    val Facebook = Color(0xFF1A57A6)
    val Discord = Color(0xFF5966F2)
    val Microsoft = Color(0xFF0078D6)
    val Yahoo = Color(0xFF660DA6)
    val Phone = Color(0xFF34C759)
}
