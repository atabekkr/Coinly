package com.atabekdev.coinly.data.transaction

import com.atabekdev.coinly.data.transaction.local.TransactionDao
import com.atabekdev.coinly.data.transaction.local.TransactionEntity
import com.atabekdev.coinly.data.transaction.local.toModel
import com.atabekdev.coinly.data.transaction.model.MonthTotals
import com.atabekdev.coinly.data.transaction.model.Transaction
import com.atabekdev.coinly.data.transaction.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock
import kotlin.uuid.Uuid

class TransactionRepository : KoinComponent {
    private val dao by inject<TransactionDao>()

    fun observeMonth(month: YearMonth): Flow<List<Transaction>> =
        dao.observeBetween(month.firstDay, month.lastDay)
            .map { transactions -> transactions.map { it.toModel() } }

    fun observeMonthTotals(month: YearMonth): Flow<MonthTotals> =
        dao.observeTotalsBetween(month.firstDay, month.lastDay).map { totals ->
            MonthTotals(
                income = totals.find { it.type == TransactionType.INCOME }?.total ?: 0L,
                expense = totals.find { it.type == TransactionType.EXPENSE }?.total ?: 0L,
            )
        }

    suspend fun getById(id: String): Transaction? = dao.getById(id)?.toModel()

    suspend fun add(
        type: TransactionType,
        amount: Long,
        categoryId: String,
        date: LocalDate,
        note: String,
    ) {
        val now = Clock.System.now().toEpochMilliseconds()
        dao.insert(
            TransactionEntity(
                id = Uuid.random().toString(),
                type = type,
                amount = amount,
                categoryId = categoryId,
                date = date,
                note = note,
                createdAt = now,
                updatedAt = now,
            )
        )
    }

    suspend fun update(
        id: String,
        type: TransactionType,
        amount: Long,
        categoryId: String,
        date: LocalDate,
        note: String,
    ) {
        dao.update(
            id = id,
            type = type,
            amount = amount,
            categoryId = categoryId,
            date = date,
            note = note,
            updatedAt = Clock.System.now().toEpochMilliseconds(),
        )
    }

    suspend fun delete(id: String) = dao.deleteById(id)
}
