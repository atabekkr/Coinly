package com.atabekdev.coinly.data.budget.local

import com.atabekdev.coinly.data.budget.model.BudgetProgress

data class BudgetProgressRow(
    val id: String,
    val categoryId: String,
    val name: String,
    val emoji: String,
    val sortOrder: Int,
    val amount: Long,
    val spent: Long,
)

fun BudgetProgressRow.toModel() = BudgetProgress(
    id = id,
    categoryId = categoryId,
    name = name,
    emoji = emoji,
    sortOrder = sortOrder,
    limit = amount,
    spent = spent,
)
