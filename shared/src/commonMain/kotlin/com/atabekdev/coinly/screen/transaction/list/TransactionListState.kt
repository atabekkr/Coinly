package com.atabekdev.coinly.screen.transaction.list

import com.atabekdev.coinly.data.transaction.model.MonthTotals
import com.atabekdev.coinly.data.transaction.model.Transaction
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

data class TransactionListState(
    val month: YearMonth,
    val currentMonth: YearMonth,
    val days: List<TransactionDay> = emptyList(),
    val totals: MonthTotals = MonthTotals(income = 0L, expense = 0L),
)

data class TransactionDay(
    val date: LocalDate,
    val transactions: List<Transaction>,
)
