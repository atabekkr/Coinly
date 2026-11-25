package com.atabekdev.coinly.data.transaction.local

data class CategoryTotal(
    val categoryId: String,
    val name: String,
    val emoji: String,
    val sortOrder: Int,
    val total: Long,
)
