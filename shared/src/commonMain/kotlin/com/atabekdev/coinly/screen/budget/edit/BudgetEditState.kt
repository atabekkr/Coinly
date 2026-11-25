package com.atabekdev.coinly.screen.budget.edit

import com.atabekdev.coinly.data.category.model.Category

data class BudgetEditState(
    val isEditMode: Boolean,
    // Whole sums as typed on the keypad, digits only.
    val amount: String = "",
    // New limit: expense categories without a limit. Edit mode: only the limit's own category.
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val isLoading: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val saveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val errorMessage: String? = null,
)
