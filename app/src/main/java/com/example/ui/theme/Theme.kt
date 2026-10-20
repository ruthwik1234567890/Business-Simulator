package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TycoonColorScheme = darkColorScheme(
    primary = BrightGold,
    onPrimary = Color(0xFF1E1B00),
    primaryContainer = AmberContainer,
    onPrimaryContainer = BrightGoldLight,
    secondary = VibrantGreen,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = VibrantGreenLight,
    tertiary = VividBlue,
    onTertiary = Color.Black,
    background = SoftDarkBg,
    onBackground = TextPureWhite,
    surface = SoftCardBg,
    onSurface = TextPureWhite,
    surfaceVariant = SoftCardElevated,
    onSurfaceVariant = TextMutedSoft,
    outline = SoftBorder,
    outlineVariant = SoftBorderHighlight,
    error = VividCoral,
    onError = Color.White
)

@Composable
fun WallStreetTycoonTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TycoonColorScheme,
        typography = Typography,
        content = content
    )
}
