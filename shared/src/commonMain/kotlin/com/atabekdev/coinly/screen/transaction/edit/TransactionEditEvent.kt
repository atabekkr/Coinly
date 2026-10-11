package com.atabekdev.coinly.screen.transaction.edit

import com.atabekdev.coinly.data.transaction.model.TransactionType
import kotlinx.datetime.LocalDate

sealed interface TransactionEditEvent {
    data class ChangeType(val type: TransactionType) : TransactionEditEvent
    data class ChangeAmount(val value: String) : TransactionEditEvent
    data class ChangeCategory(val categoryId: String) : TransactionEditEvent
    data object OnDateClick : TransactionEditEvent
    data class ChangeDate(val date: LocalDate) : TransactionEditEvent
    data class ChangeNote(val value: String) : TransactionEditEvent
    data object OnSaveClick : TransactionEditEvent
    data object OnDeleteClick : TransactionEditEvent
    data object OnDeleteConfirm : TransactionEditEvent
    data object Idle : TransactionEditEvent
}
