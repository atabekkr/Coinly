package com.atabekdev.coinly.data.category.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.atabekdev.coinly.data.category.model.Category
import com.atabekdev.coinly.data.transaction.model.TransactionType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val emoji: String,
    val type: TransactionType,
    val sortOrder: Int,
    val createdAt: Long,
    val updatedAt: Long,
)

fun CategoryEntity.toModel() = Category(
    id = id,
    name = name,
    emoji = emoji,
    type = type,
)
