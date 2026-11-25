package com.atabekdev.coinly.core.database

import androidx.room3.AutoMigration
import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.atabekdev.coinly.data.budget.local.BudgetDao
import com.atabekdev.coinly.data.budget.local.BudgetEntity
import com.atabekdev.coinly.data.category.local.CategoryDao
import com.atabekdev.coinly.data.category.local.CategoryEntity
import com.atabekdev.coinly.data.transaction.local.TransactionDao
import com.atabekdev.coinly.data.transaction.local.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

const val DATABASE_NAME = "coinly.db"

@Database(
    entities = [CategoryEntity::class, TransactionEntity::class, BudgetEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
)
@ColumnTypeConverters(DateConverters::class)
@ConstructedBy(CoinlyDatabaseConstructor::class)
abstract class CoinlyDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
}

@Suppress("KotlinNoActualForExpect")
expect object CoinlyDatabaseConstructor : RoomDatabaseConstructor<CoinlyDatabase> {
    override fun initialize(): CoinlyDatabase
}

// A schema change bumps the version and adds a migration. No destructive fallback: a missing
// migration must fail instead of wiping the user's data.
fun createDatabase(builder: RoomDatabase.Builder<CoinlyDatabase>): CoinlyDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addCallback(PrepopulateCallback())
        .build()
