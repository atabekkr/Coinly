package com.atabekdev.coinly.data.transaction.local

import com.atabekdev.coinly.data.transaction.model.TransactionType

data class TransactionTotal(
    val type: TransactionType,
    val total: Long,
)
