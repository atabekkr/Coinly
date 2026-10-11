package com.atabekdev.coinly.data.transaction.model

import com.atabekdev.coinly.data.category.model.Category
import kotlinx.datetime.LocalDate

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Long,
    val category: Category,
    val date: LocalDate,
    val note: String,
)
