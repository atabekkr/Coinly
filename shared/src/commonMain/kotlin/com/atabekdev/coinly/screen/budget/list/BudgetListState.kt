package com.atabekdev.coinly.screen.budget.list

import com.atabekdev.coinly.data.budget.model.BudgetProgress
import kotlinx.datetime.YearMonth

data class BudgetListState(
    // Limits are shown for the current month only.
    val currentMonth: YearMonth,
    // Days of the month after today.
    val daysLeft: Int,
    val budgets: List<BudgetProgress> = emptyList(),
    // Sum of all limits, and what was spent in the categories that have one.
    val totalLimit: Long = 0L,
    val totalSpent: Long = 0L,
)
