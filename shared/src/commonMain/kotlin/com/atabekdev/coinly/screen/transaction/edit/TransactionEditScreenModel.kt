package com.atabekdev.coinly.screen.transaction.edit

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.atabekdev.coinly.core.common.util.convertToSums
import com.atabekdev.coinly.core.common.util.convertToTiyin
import com.atabekdev.coinly.data.category.CategoryRepository
import com.atabekdev.coinly.data.transaction.TransactionRepository
import com.atabekdev.coinly.data.transaction.model.TransactionType
import com.atabekdev.coinly.ui.localization.AppRes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock

private const val MAX_AMOUNT_DIGITS = 12
internal const val MAX_NOTE_LENGTH = 200

class TransactionEditScreenModel(
    private val transactionId: String?,
) : StateScreenModel<TransactionEditState>(
    TransactionEditState(isEditMode = transactionId != null, date = today())
), KoinComponent {
    private val transactionRepository by inject<TransactionRepository>()
    private val categoryRepository by inject<CategoryRepository>()
    private val strings = AppRes.strings.transaction

    init {
        screenModelScope.launch {
            categoryRepository.observeAll().collect { categories ->
                mutableState.update { it.copy(categories = categories) }
            }
        }
        if (transactionId != null) load(transactionId)
    }

    fun onEvent(event: TransactionEditEvent) {
        when (event) {
            is TransactionEditEvent.ChangeType -> setState(type = event.type)
            is TransactionEditEvent.ChangeAmount -> changeAmount(event.value)
            is TransactionEditEvent.ChangeCategory -> setState(selectedCategoryId = event.categoryId)
            TransactionEditEvent.OnDateClick -> setState(showDatePicker = true)
            is TransactionEditEvent.ChangeDate -> setState(date = event.date)
            is TransactionEditEvent.ChangeNote -> changeNote(event.value)
            TransactionEditEvent.OnSaveClick -> save()
            TransactionEditEvent.OnDeleteClick -> setState(showDeleteDialog = true)
            TransactionEditEvent.OnDeleteConfirm -> delete()
            TransactionEditEvent.Idle -> setState()
        }
    }

    private fun load(id: String) {
        setState(loading = true)
        screenModelScope.launch {
            val transaction = transactionRepository.getById(id)
            if (transaction == null) {
                setState(errorMessage = strings.errorNotFound)
                return@launch
            }
            setState(
                type = transaction.type,
                amount = convertToSums(transaction.amount).toString(),
                selectedCategoryId = transaction.category.id,
                date = transaction.date,
                note = transaction.note,
            )
        }
    }

    private fun changeAmount(value: String) {
        val digits = value.filter { it.isDigit() }.trimStart('0')
        if (digits.length > MAX_AMOUNT_DIGITS) return
        setState(amount = digits)
    }

    private fun changeNote(value: String) {
        if (value.length > MAX_NOTE_LENGTH) return
        setState(note = value)
    }

    private fun save() {
        val current = state.value
        val amount = current.amount.toLongOrNull()
        if (amount == null || amount <= 0) {
            setState(errorMessage = strings.errorEmptyAmount)
            return
        }
        // The selection is kept when the type changes, so it may belong to the other type.
        val categoryId = current.categories
            .find { it.id == current.selectedCategoryId && it.type == current.type }
            ?.id
        if (categoryId == null) {
            setState(errorMessage = strings.errorNoCategory)
            return
        }
        if (current.date > today()) {
            setState(errorMessage = strings.errorFutureDate)
            return
        }

        setState(loading = true)
        screenModelScope.launch {
            try {
                if (transactionId == null) {
                    transactionRepository.add(
                        type = current.type,
                        amount = convertToTiyin(amount),
                        categoryId = categoryId,
                        date = current.date,
                        note = current.note.trim(),
                    )
                } else {
                    transactionRepository.update(
                        id = transactionId,
                        type = current.type,
                        amount = convertToTiyin(amount),
                        categoryId = categoryId,
                        date = current.date,
                        note = current.note.trim(),
                    )
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
        val id = transactionId ?: return
        setState(loading = true)
        screenModelScope.launch {
            try {
                transactionRepository.delete(id)
                setState(deleteSuccess = true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                setState(errorMessage = strings.errorDelete)
            }
        }
    }

    private fun setState(
        type: TransactionType? = null,
        amount: String? = null,
        selectedCategoryId: String? = null,
        date: LocalDate? = null,
        note: String? = null,
        loading: Boolean = false,
        showDatePicker: Boolean = false,
        showDeleteDialog: Boolean = false,
        saveSuccess: Boolean = false,
        deleteSuccess: Boolean = false,
        errorMessage: String? = null,
    ) {
        mutableState.update {
            it.copy(
                type = type ?: it.type,
                amount = amount ?: it.amount,
                selectedCategoryId = selectedCategoryId ?: it.selectedCategoryId,
                date = date ?: it.date,
                note = note ?: it.note,
                isLoading = loading,
                showDatePicker = showDatePicker,
                showDeleteDialog = showDeleteDialog,
                saveSuccess = saveSuccess,
                deleteSuccess = deleteSuccess,
                errorMessage = errorMessage,
            )
        }
    }
}

private fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
