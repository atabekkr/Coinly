package com.atabekdev.coinly.screen.report

import com.atabekdev.coinly.data.transaction.model.TransactionType

sealed interface ReportEvent {
    data class ChangeType(val type: TransactionType) : ReportEvent
    data object OnPreviousMonthClick : ReportEvent
    data object OnNextMonthClick : ReportEvent
    data object Idle : ReportEvent
}
