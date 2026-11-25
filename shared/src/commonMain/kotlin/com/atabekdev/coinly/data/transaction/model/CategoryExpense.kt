package com.atabekdev.coinly.data.transaction.model

data class CategoryExpense(
    val categoryId: String,
    val name: String,
    val emoji: String,
    val sortOrder: Int,
    val amount: Long,
)
