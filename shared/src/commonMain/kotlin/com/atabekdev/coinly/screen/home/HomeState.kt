package com.atabekdev.coinly.screen.home

import com.atabekdev.coinly.data.transaction.model.MonthTotals
import com.atabekdev.coinly.data.transaction.model.Transaction
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

data class HomeState(
    val month: YearMonth,
    val currentMonth: YearMonth,
    // Income minus expenses over all time; does not depend on the chosen month.
    val balance: Long = 0L,
    val totals: MonthTotals = MonthTotals(income = 0L, expense = 0L),
    // The newest transactions of the chosen month, grouped by day.
    val days: List<HomeDay> = emptyList(),
)

data class HomeDay(
    val date: LocalDate,
    val transactions: List<Transaction>,
)
