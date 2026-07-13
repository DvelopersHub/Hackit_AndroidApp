package com.kitdevelopershub.hackit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = HackitColors.HackitOrange,
    onPrimary = Color.White,
    secondary = HackitColors.HighlightYellow,
    onSecondary = HackitColors.Charcoal,
    background = HackitColors.WarmOffWhite,
    onBackground = HackitColors.Charcoal,
    surface = Color.White,
    onSurface = HackitColors.Charcoal,
    surfaceVariant = HackitColors.WarmOffWhite,
    onSurfaceVariant = HackitColors.MidGray,
    outline = HackitColors.LightGray,
    error = HackitColors.StatusError,
    onError = Color.White,
)

/**
 * Hackit ブランドテーマ。MVP は iOS と同じくライト固定
 * （dynamic color はブランド色が崩れるため使わない）。
 */
@Composable
fun HackitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = HackitTypography,
        content = content,
    )
}
