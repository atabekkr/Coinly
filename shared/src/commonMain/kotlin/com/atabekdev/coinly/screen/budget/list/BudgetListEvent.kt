package com.atabekdev.coinly.screen.budget.list

sealed interface BudgetListEvent {
    data object Idle : BudgetListEvent
}
