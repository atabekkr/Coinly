package com.atabekdev.coinly.data.budget.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.atabekdev.coinly.data.transaction.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface BudgetDao {

    // Every limit with what was spent in its category between the dates (0 when nothing).
    @Query(
        "SELECT b.id AS id, c.id AS categoryId, c.name AS name, c.emoji AS emoji, " +
            "c.sortOrder AS sortOrder, b.amount AS amount, COALESCE(SUM(t.amount), 0) AS spent " +
            "FROM budgets b INNER JOIN categories c ON c.id = b.categoryId " +
            "LEFT JOIN transactions t ON t.categoryId = b.categoryId " +
            "AND t.type = :expense AND t.date BETWEEN :from AND :to " +
            "GROUP BY b.id ORDER BY c.sortOrder"
    )
    fun observeProgressBetween(
        expense: TransactionType,
        from: LocalDate,
        to: LocalDate,
    ): Flow<List<BudgetProgressRow>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getById(id: String): BudgetEntity?

    @Query("SELECT categoryId FROM budgets")
    suspend fun getCategoryIds(): List<String>

    @Insert
    suspend fun insert(budget: BudgetEntity)

    @Query("UPDATE budgets SET amount = :amount, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateAmount(id: String, amount: Long, updatedAt: Long)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: String)
}
