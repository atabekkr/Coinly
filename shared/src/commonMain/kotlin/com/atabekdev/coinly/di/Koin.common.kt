package com.atabekdev.coinly.di

import com.atabekdev.coinly.core.database.CoinlyDatabase
import com.atabekdev.coinly.core.database.createDatabase
import com.atabekdev.coinly.data.category.CategoryRepository
import com.atabekdev.coinly.data.transaction.TransactionRepository
import com.atabekdev.coinly.screen.home.HomeScreenModel
import com.atabekdev.coinly.screen.transaction.edit.TransactionEditScreenModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            platformModule(),
            databaseModule(),
            repositoryModule(),
            screenModelModule(),
        )
    }
}

expect fun platformModule(): Module

fun databaseModule() = module {
    single { createDatabase(get()) }
    single { get<CoinlyDatabase>().categoryDao() }
    single { get<CoinlyDatabase>().transactionDao() }
}

fun repositoryModule() = module {
    single { CategoryRepository() }
    single { TransactionRepository() }
}

fun screenModelModule() = module {
    factory { HomeScreenModel() }
    factory { params -> TransactionEditScreenModel(params.getOrNull()) }
}
