package com.atabekdev.coinly.screen.budget.edit

sealed interface BudgetEditEvent {
    data class ChangeAmount(val value: String) : BudgetEditEvent
    data class ChangeCategory(val categoryId: String) : BudgetEditEvent
    data object OnSaveClick : BudgetEditEvent
    data object OnDeleteClick : BudgetEditEvent
    data object OnDeleteConfirm : BudgetEditEvent
    data object Idle : BudgetEditEvent
}
