package com.atabekdev.coinly.data.transaction.local

import androidx.room3.Embedded
import androidx.room3.Relation
import com.atabekdev.coinly.data.category.local.CategoryEntity
import com.atabekdev.coinly.data.category.local.toModel
import com.atabekdev.coinly.data.transaction.model.Transaction

data class TransactionWithCategory(
    @Embedded val transaction: TransactionEntity,
    @Relation(parentColumns = ["categoryId"], entityColumns = ["id"])
    val category: CategoryEntity,
)

fun TransactionWithCategory.toModel() = Transaction(
    id = transaction.id,
    type = transaction.type,
    amount = transaction.amount,
    category = category.toModel(),
    date = transaction.date,
    note = transaction.note,
)
