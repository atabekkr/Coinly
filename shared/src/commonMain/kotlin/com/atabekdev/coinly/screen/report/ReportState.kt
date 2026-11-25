package com.atabekdev.coinly.screen.report

import com.atabekdev.coinly.data.transaction.model.CategoryExpense
import com.atabekdev.coinly.data.transaction.model.TransactionType
import kotlinx.datetime.YearMonth

data class ReportState(
    val month: YearMonth,
    val currentMonth: YearMonth,
    val type: TransactionType = TransactionType.EXPENSE,
    val isLoading: Boolean = false,
    // Totals of the chosen type by category, biggest first.
    val categories: List<CategoryExpense> = emptyList(),
    val total: Long = 0L,
    val dailyAverage: Long = 0L,
)
