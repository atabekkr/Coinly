package com.atabekdev.coinly.core.common.util

import kotlin.math.abs

private const val TIYIN_IN_SUM = 100L
private const val THOUSANDS_SEPARATOR = ' '

fun convertToTiyin(sums: Long): Long = sums * TIYIN_IN_SUM

fun convertToSums(tiyin: Long): Long = tiyin / TIYIN_IN_SUM

// 30_000_000 tiyin → "300 000", 150_050 → "1 500,50", -500_000 → "-5 000".
fun formatBalance(tiyin: Long): String {
    val sign = if (tiyin < 0) "-" else ""
    val sums = abs(tiyin / TIYIN_IN_SUM).toString()
        .reversed()
        .chunked(3)
        .joinToString(THOUSANDS_SEPARATOR.toString())
        .reversed()
    val rest = abs(tiyin % TIYIN_IN_SUM)
    return if (rest == 0L) "$sign$sums" else "$sign$sums,${rest.toString().padStart(2, '0')}"
}
