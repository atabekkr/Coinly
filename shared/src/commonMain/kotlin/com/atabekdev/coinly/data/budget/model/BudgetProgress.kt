package com.atabekdev.coinly.data.budget.model

// A category's limit and what was spent in the category during a month.
data class BudgetProgress(
    val id: String,
    val categoryId: String,
    val name: String,
    val emoji: String,
    val sortOrder: Int,
    val limit: Long,
    val spent: Long,
)
