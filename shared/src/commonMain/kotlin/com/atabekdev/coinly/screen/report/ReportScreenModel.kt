package com.atabekdev.coinly.screen.report

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.atabekdev.coinly.core.common.util.convertToSums
import com.atabekdev.coinly.core.common.util.convertToTiyin
import com.atabekdev.coinly.data.transaction.TransactionRepository
import com.atabekdev.coinly.data.transaction.model.TransactionType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minusMonth
import kotlinx.datetime.plusMonth
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock

class ReportScreenModel : StateScreenModel<ReportState>(
    today().yearMonth.let { ReportState(month = it, currentMonth = it) }
), KoinComponent {
    private val transactionRepository by inject<TransactionRepository>()
    private var observeJob: Job? = null

    init {
        observeTotals(state.value.type, state.value.month)
    }

    fun onEvent(event: ReportEvent) {
        when (event) {
            is ReportEvent.ChangeType -> {
                setState(type = event.type)
                observeTotals(event.type, state.value.month)
            }
            ReportEvent.OnPreviousMonthClick -> changeMonth(state.value.month.minusMonth())
            ReportEvent.OnNextMonthClick -> {
                val next = state.value.month.plusMonth()
                // Future dates cannot be picked, so months after the current one are always empty.
                if (next <= state.value.currentMonth) changeMonth(next)
            }
            ReportEvent.Idle -> setState()
        }
    }

    private fun changeMonth(month: YearMonth) {
        setState(month = month)
        observeTotals(state.value.type, month)
    }

    private fun observeTotals(type: TransactionType, month: YearMonth) {
        observeJob?.cancel()
        observeJob = screenModelScope.launch {
            transactionRepository.observeMonthTotalsByCategory(type, month).collect { categories ->
                val total = categories.sumOf { it.amount }
                // Rounded down to whole sums.
                val dailyAverage = convertToTiyin(convertToSums(total / daysOf(month)))
                mutableState.update {
                    it.copy(categories = categories, total = total, dailyAverage = dailyAverage)
                }
            }
        }
    }

    // The current month is averaged over the days passed so far, a past month over all its days.
    private fun daysOf(month: YearMonth): Int {
        val today = today()
        return if (month == today.yearMonth) today.day else month.lastDay.day
    }

    private fun setState(
        month: YearMonth? = null,
        type: TransactionType? = null,
        loading: Boolean = false,
    ) {
        mutableState.update {
            it.copy(month = month ?: it.month, type = type ?: it.type, isLoading = loading)
        }
    }
}

private fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
