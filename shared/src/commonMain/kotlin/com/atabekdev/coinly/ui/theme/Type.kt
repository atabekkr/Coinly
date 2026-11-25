package com.atabekdev.coinly.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coinly.shared.generated.resources.Res
import coinly.shared.generated.resources.inter_bold
import coinly.shared.generated.resources.inter_medium
import coinly.shared.generated.resources.inter_regular
import coinly.shared.generated.resources.inter_semibold
import org.jetbrains.compose.resources.Font

// Inter for everything: the design's heading font (Space Grotesk) has no Cyrillic.
// Sizes follow docs/design/README.md, "Type".
@Composable
internal fun coinlyTypography(): Typography {
    val inter = FontFamily(
        Font(Res.font.inter_regular, FontWeight.Normal),
        Font(Res.font.inter_medium, FontWeight.Medium),
        Font(Res.font.inter_semibold, FontWeight.SemiBold),
        Font(Res.font.inter_bold, FontWeight.Bold),
    )

    fun style(size: Int, lineHeight: Int, weight: FontWeight, tracking: Double = 0.0) = TextStyle(
        fontFamily = inter,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        letterSpacing = tracking.em,
    )

    return Typography(
        // Total balance.
        displayLarge = style(44, 52, FontWeight.Bold, -0.02),
        // Entered amount, by length: displayMedium → displaySmall → headlineLarge.
        displayMedium = style(52, 56, FontWeight.Bold, -0.02),
        displaySmall = style(40, 48, FontWeight.Bold, -0.02),
        headlineLarge = style(28, 36, FontWeight.Bold, -0.01),
        // Keypad digits.
        headlineMedium = style(22, 28, FontWeight.Medium),
        // Screen title.
        headlineSmall = style(26, 32, FontWeight.Bold, -0.01),
        // Section heading, top bar title.
        titleLarge = style(18, 24, FontWeight.SemiBold),
        // Row amount, primary button.
        titleMedium = style(16, 24, FontWeight.SemiBold),
        // Row title.
        titleSmall = style(15, 20, FontWeight.SemiBold),
        bodyLarge = style(16, 24, FontWeight.Normal),
        bodyMedium = style(14, 20, FontWeight.Normal),
        // Secondary line, card label.
        bodySmall = style(13, 18, FontWeight.Normal),
        // Pill, field button, segmented control.
        labelLarge = style(14, 20, FontWeight.Medium),
        // Percent, small labels.
        labelMedium = style(12, 16, FontWeight.Medium),
        // Tab label, category tile label.
        labelSmall = style(11, 14, FontWeight.Medium),
    )
}
