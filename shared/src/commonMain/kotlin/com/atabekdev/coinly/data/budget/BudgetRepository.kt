package com.atabekdev.coinly.data.budget

import com.atabekdev.coinly.data.budget.local.BudgetDao
import com.atabekdev.coinly.data.budget.local.BudgetEntity
import com.atabekdev.coinly.data.budget.local.toModel
import com.atabekdev.coinly.data.budget.model.Budget
import com.atabekdev.coinly.data.budget.model.BudgetProgress
import com.atabekdev.coinly.data.transaction.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.YearMonth
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock
import kotlin.uuid.Uuid

class BudgetRepository : KoinComponent {
    private val dao by inject<BudgetDao>()

    fun observeMonthProgress(month: YearMonth): Flow<List<BudgetProgress>> =
        dao.observeProgressBetween(TransactionType.EXPENSE, month.firstDay, month.lastDay)
            .map { rows -> rows.map { it.toModel() } }

    suspend fun getById(id: String): Budget? = dao.getById(id)?.toModel()

    // Ids of the categories that already have a limit.
    suspend fun getBudgetedCategoryIds(): List<String> = dao.getCategoryIds()

    suspend fun add(categoryId: String, amount: Long) {
        val now = Clock.System.now().toEpochMilliseconds()
        dao.insert(
            BudgetEntity(
                id = Uuid.random().toString(),
                categoryId = categoryId,
                amount = amount,
                createdAt = now,
                updatedAt = now,
            )
        )
    }

    suspend fun updateAmount(id: String, amount: Long) {
        dao.updateAmount(id = id, amount = amount, updatedAt = Clock.System.now().toEpochMilliseconds())
    }

    suspend fun delete(id: String) = dao.deleteById(id)
}
