package com.atabekdev.coinly.screen.transaction.list

sealed interface TransactionListEvent {
    data object OnPreviousMonthClick : TransactionListEvent
    data object OnNextMonthClick : TransactionListEvent
    data object Idle : TransactionListEvent
}
