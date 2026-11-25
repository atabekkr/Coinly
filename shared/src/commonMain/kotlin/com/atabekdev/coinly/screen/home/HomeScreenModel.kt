package com.atabekdev.coinly.screen.home

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.atabekdev.coinly.data.transaction.TransactionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plusMonth
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock

// How many of the month's newest transactions Home shows; the full list is behind "see all".
private const val RECENT_TRANSACTIONS_LIMIT = 10

class HomeScreenModel : StateScreenModel<HomeState>(
    currentMonth().let { HomeState(month = it, currentMonth = it) }
), KoinComponent {
    private val transactionRepository by inject<TransactionRepository>()
    private var observeJob: Job? = null

    init {
        screenModelScope.launch {
            transactionRepository.observeBalance().collect { balance ->
                mutableState.update { it.copy(balance = balance) }
            }
        }
        observeMonth(state.value.month)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.OnPreviousMonthClick -> changeMonth(state.value.month.minusMonth())
            HomeEvent.OnNextMonthClick -> {
                val next = state.value.month.plusMonth()
                // Future dates cannot be picked, so months after the current one are always empty.
                if (next <= state.value.currentMonth) changeMonth(next)
            }
            HomeEvent.Idle -> setState()
        }
    }

    private fun changeMonth(month: YearMonth) {
        setState(month = month)
        observeMonth(month)
    }

    private fun observeMonth(month: YearMonth) {
        observeJob?.cancel()
        observeJob = screenModelScope.launch {
            combine(
                transactionRepository.observeMonth(month),
                transactionRepository.observeMonthTotals(month),
            ) { transactions, totals ->
                val days = transactions
                    .take(RECENT_TRANSACTIONS_LIMIT)
                    .groupBy { it.date }
                    .map { (date, dayTransactions) -> HomeDay(date, dayTransactions) }
                days to totals
            }.collect { (days, totals) ->
                mutableState.update { it.copy(days = days, totals = totals) }
            }
        }
    }

    private fun setState(month: YearMonth? = null) {
        mutableState.update { it.copy(month = month ?: it.month) }
    }
}

private fun currentMonth(): YearMonth = Clock.System.todayIn(TimeZone.currentSystemDefault()).yearMonth
