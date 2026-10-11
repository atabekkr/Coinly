package com.atabekdev.coinly.data.transaction.local

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.atabekdev.coinly.data.category.local.CategoryEntity
import com.atabekdev.coinly.data.transaction.model.TransactionType
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("categoryId"), Index("date")],
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: TransactionType,
    val amount: Long,
    val categoryId: String,
    val date: LocalDate,
    val note: String,
    val createdAt: Long,
    val updatedAt: Long,
)
