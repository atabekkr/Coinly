package com.atabekdev.coinly.data.budget.local

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.atabekdev.coinly.data.budget.model.Budget
import com.atabekdev.coinly.data.category.local.CategoryEntity

// A monthly limit of one expense category; it applies to every month until changed.
@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index(value = ["categoryId"], unique = true)],
)
data class BudgetEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val amount: Long,
    val createdAt: Long,
    val updatedAt: Long,
)

fun BudgetEntity.toModel() = Budget(
    id = id,
    categoryId = categoryId,
    amount = amount,
)
