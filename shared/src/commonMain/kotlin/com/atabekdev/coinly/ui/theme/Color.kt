package com.atabekdev.coinly.ui.theme

import androidx.compose.ui.graphics.Color

// Design tokens (docs/design/README.md, "Tokens"). Screens read them through
// MaterialTheme.colorScheme (see Theme.kt) or the named colors below.
internal val BrandPrimary = Color(0xFF6246E0)
internal val BrandPrimaryTint = Color(0xFFECE8FC)
internal val BrandBackground = Color(0xFFF3F2F7)
internal val BrandSurface = Color(0xFFFFFFFF)
internal val BrandText = Color(0xFF16132B)
internal val BrandTextSecondary = Color(0xFF5F5B73)
internal val BrandDivider = Color(0xFFECEAF3)
internal val BrandTrack = Color(0xFFE6E4EE)
internal val BrandOutline = Color(0xFFB9B6C9)
internal val BrandDanger = Color(0xFFB42318)
internal val BrandDangerTint = Color(0xFFFBE3E0)

// On the violet hero: pills and round buttons, secondary labels, the currency next to the balance.
val HeroOverlayColor = BrandText.copy(alpha = 0.22f)
// Track of the progress bar on a violet card.
val HeroTrackColor = BrandText.copy(alpha = 0.28f)
val HeroSecondaryColor = Color(0xFFE7E2FF)
val HeroTertiaryColor = Color(0xFFCFC6FF)

// Material 3 color scheme has no "positive" role; income amounts use this green.
val IncomeColor = Color(0xFF0B7A55)
val IncomeTintColor = Color(0xFFDFF3EA)

// Accent of the expense summary icon; expense amounts themselves stay onSurface.
val ExpenseAccentColor = Color(0xFFB4500F)
val ExpenseTintColor = Color(0xFFFCE9DC)

// Bar of a limit that is close to being reached (track: ExpenseTintColor, text: ExpenseAccentColor).
val WarningBarColor = Color(0xFFE8823A)

// Category colors for charts: a category takes CategoryColors[sortOrder % size], so its color
// never depends on the data. Index 8 is the last default expense category ("Другое"): gray.
val CategoryColors = listOf(
    Color(0xFF2F7DE1),
    Color(0xFFE8823A),
    Color(0xFF6246E0),
    Color(0xFFD9A514),
    Color(0xFF2FA36B),
    Color(0xFFD6457A),
    Color(0xFF1E9E8A),
    Color(0xFF9C755F),
    Color(0xFFB9B6C9),
    Color(0xFFE15759),
)

fun categoryColor(sortOrder: Int): Color = CategoryColors[sortOrder % CategoryColors.size]

// Background of a category's emoji tile: its chart color, washed out.
fun categoryTint(sortOrder: Int): Color = categoryColor(sortOrder).copy(alpha = 0.16f)
