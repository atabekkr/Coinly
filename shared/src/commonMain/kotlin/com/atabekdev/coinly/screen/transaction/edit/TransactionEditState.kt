package com.atabekdev.coinly.screen.transaction.edit

import com.atabekdev.coinly.data.category.model.Category
import com.atabekdev.coinly.data.transaction.model.TransactionType
import kotlinx.datetime.LocalDate

data class TransactionEditState(
    val isEditMode: Boolean,
    val date: LocalDate,
    val type: TransactionType = TransactionType.EXPENSE,
    // Whole sums as typed, digits only; formatted by AmountVisualTransformation.
    val amount: String = "",
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val note: String = "",
    val isLoading: Boolean = false,
    val showDatePicker: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val saveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val errorMessage: String? = null,
)
