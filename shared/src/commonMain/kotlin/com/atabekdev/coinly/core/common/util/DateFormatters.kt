package com.atabekdev.coinly.core.common.util

import com.atabekdev.coinly.ui.localization.AppRes
import kotlinx.datetime.LocalDate

// 2026-10-11 → "11 октября 2026"
fun formatDate(date: LocalDate): String {
    val month = AppRes.strings.common.monthsGenitive[date.month.ordinal]
    return "${date.day} $month ${date.year}"
}
