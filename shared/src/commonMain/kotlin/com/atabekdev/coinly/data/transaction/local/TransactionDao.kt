package com.atabekdev.coinly.data.transaction.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import com.atabekdev.coinly.data.transaction.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface TransactionDao {

    @Transaction
    @Query("SELECT * FROM transactions WHERE date BETWEEN :from AND :to ORDER BY date DESC, createdAt DESC")
    fun observeBetween(from: LocalDate, to: LocalDate): Flow<List<TransactionWithCategory>>

    @Query("SELECT type, SUM(amount) AS total FROM transactions WHERE date BETWEEN :from AND :to GROUP BY type")
    fun observeTotalsBetween(from: LocalDate, to: LocalDate): Flow<List<TransactionTotal>>

    // Income minus expenses over all time; 0 when there are no transactions.
    @Query("SELECT COALESCE(SUM(CASE WHEN type = :income THEN amount ELSE -amount END), 0) FROM transactions")
    fun observeBalance(income: TransactionType): Flow<Long>

    @Query(
        "SELECT c.id AS categoryId, c.name AS name, c.emoji AS emoji, c.sortOrder AS sortOrder, " +
            "SUM(t.amount) AS total " +
            "FROM transactions t INNER JOIN categories c ON c.id = t.categoryId " +
            "WHERE t.type = :type AND t.date BETWEEN :from AND :to " +
            "GROUP BY c.id ORDER BY total DESC, c.sortOrder"
    )
    fun observeTotalsByCategoryBetween(
        type: TransactionType,
        from: LocalDate,
        to: LocalDate,
    ): Flow<List<CategoryTotal>>

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): TransactionWithCategory?

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query(
        "UPDATE transactions SET type = :type, amount = :amount, categoryId = :categoryId, " +
            "date = :date, note = :note, updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun update(
        id: String,
        type: TransactionType,
        amount: Long,
        categoryId: String,
        date: LocalDate,
        note: String,
        updatedAt: Long,
    )

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String)
}
