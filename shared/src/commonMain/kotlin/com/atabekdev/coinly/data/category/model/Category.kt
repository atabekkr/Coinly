package com.atabekdev.coinly.data.category.model

import com.atabekdev.coinly.data.transaction.model.TransactionType

data class Category(
    val id: String,
    val name: String,
    val emoji: String,
    val type: TransactionType,
)
