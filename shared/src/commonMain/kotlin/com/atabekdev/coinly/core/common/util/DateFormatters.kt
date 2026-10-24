package com.atabekdev.coinly.core.common.util

import com.atabekdev.coinly.ui.localization.AppRes
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

// 2026-10-11 → "11 октября 2026"
fun formatDate(date: LocalDate): String {
    val month = AppRes.strings.common.monthsGenitive[date.month.ordinal]
    return "${date.day} $month ${date.year}"
}

// 2026-10-11 → "11 октября"
fun formatDayMonth(date: LocalDate): String {
    val month = AppRes.strings.common.monthsGenitive[date.month.ordinal]
    return "${date.day} $month"
}

// 2026-10 → "Октябрь 2026"
fun formatMonth(month: YearMonth): String {
    val name = AppRes.strings.common.monthsNominative[month.month.ordinal]
    return "$name ${month.year}"
}
