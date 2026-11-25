package com.atabekdev.coinly.screen.budget.list

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.atabekdev.coinly.data.budget.BudgetRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock

class BudgetListScreenModel : StateScreenModel<BudgetListState>(
    Clock.System.todayIn(TimeZone.currentSystemDefault()).let { today ->
        BudgetListState(
            currentMonth = today.yearMonth,
            daysLeft = today.yearMonth.lastDay.day - today.day,
        )
    }
), KoinComponent {
    private val budgetRepository by inject<BudgetRepository>()

    init {
        screenModelScope.launch {
            budgetRepository.observeMonthProgress(state.value.currentMonth).collect { budgets ->
                mutableState.update {
                    it.copy(
                        budgets = budgets,
                        totalLimit = budgets.sumOf { budget -> budget.limit },
                        totalSpent = budgets.sumOf { budget -> budget.spent },
                    )
                }
            }
        }
    }

    fun onEvent(event: BudgetListEvent) {
        when (event) {
            BudgetListEvent.Idle -> setState()
        }
    }

    // No one-shot flags yet; every field is set once or written by the Flow collector.
    private fun setState() {
        mutableState.update { it.copy() }
    }
}
