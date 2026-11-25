package com.atabekdev.coinly.screen.budget.edit

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.atabekdev.coinly.core.common.util.convertToSums
import com.atabekdev.coinly.core.common.util.convertToTiyin
import com.atabekdev.coinly.data.budget.BudgetRepository
import com.atabekdev.coinly.data.category.CategoryRepository
import com.atabekdev.coinly.data.transaction.model.TransactionType
import com.atabekdev.coinly.ui.localization.AppRes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val MAX_AMOUNT_DIGITS = 12

class BudgetEditScreenModel(
    private val budgetId: String?,
) : StateScreenModel<BudgetEditState>(
    BudgetEditState(isEditMode = budgetId != null)
), KoinComponent {
    private val budgetRepository by inject<BudgetRepository>()
    private val categoryRepository by inject<CategoryRepository>()
    private val strings = AppRes.strings.budget

    init {
        load()
    }

    fun onEvent(event: BudgetEditEvent) {
        when (event) {
            is BudgetEditEvent.ChangeAmount -> changeAmount(event.value)
            is BudgetEditEvent.ChangeCategory -> setState(selectedCategoryId = event.categoryId)
            BudgetEditEvent.OnSaveClick -> save()
            BudgetEditEvent.OnDeleteClick -> setState(showDeleteDialog = true)
            BudgetEditEvent.OnDeleteConfirm -> delete()
            BudgetEditEvent.Idle -> setState()
        }
    }

    private fun load() {
        screenModelScope.launch {
            val budget = budgetId?.let { budgetRepository.getById(it) }
            if (budgetId != null && budget == null) {
                setState(errorMessage = strings.errorNotFound)
                return@launch
            }
            if (budget != null) {
                setState(
                    amount = convertToSums(budget.amount).toString(),
                    selectedCategoryId = budget.categoryId,
                )
            }
            val budgetedIds = budgetRepository.getBudgetedCategoryIds()
            categoryRepository.observeAll().collect { categories ->
                val available = categories.filter { category ->
                    if (budget != null) {
                        // The category of an existing limit cannot be changed.
                        category.id == budget.categoryId
                    } else {
                        category.type == TransactionType.EXPENSE && category.id !in budgetedIds
                    }
                }
                mutableState.update { it.copy(categories = available) }
            }
        }
    }

    private fun changeAmount(value: String) {
        val digits = value.filter { it.isDigit() }.trimStart('0')
        if (digits.length > MAX_AMOUNT_DIGITS) return
        setState(amount = digits)
    }

    private fun save() {
        val current = state.value
        val amount = current.amount.toLongOrNull()
        if (amount == null || amount <= 0) {
            setState(errorMessage = strings.errorEmptyAmount)
            return
        }
        val categoryId = current.categories.find { it.id == current.selectedCategoryId }?.id
        if (categoryId == null) {
            setState(errorMessage = strings.errorNoCategory)
            return
        }

        setState(loading = true)
        screenModelScope.launch {
            try {
                if (budgetId == null) {
                    budgetRepository.add(categoryId = categoryId, amount = convertToTiyin(amount))
                } else {
                    budgetRepository.updateAmount(id = budgetId, amount = convertToTiyin(amount))
                }
                setState(saveSuccess = true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                setState(errorMessage = strings.errorSave)
            }
        }
    }

    private fun delete() {
        val id = budgetId ?: return
        setState(loading = true)
        screenModelScope.launch {
            try {
                budgetRepository.delete(id)
                setState(deleteSuccess = true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                setState(errorMessage = strings.errorDelete)
            }
        }
    }

    private fun setState(
        amount: String? = null,
        selectedCategoryId: String? = null,
        loading: Boolean = false,
        showDeleteDialog: Boolean = false,
        saveSuccess: Boolean = false,
        deleteSuccess: Boolean = false,
        errorMessage: String? = null,
    ) {
        mutableState.update {
            it.copy(
                amount = amount ?: it.amount,
                selectedCategoryId = selectedCategoryId ?: it.selectedCategoryId,
                isLoading = loading,
                showDeleteDialog = showDeleteDialog,
                saveSuccess = saveSuccess,
                deleteSuccess = deleteSuccess,
                errorMessage = errorMessage,
            )
        }
    }
}
