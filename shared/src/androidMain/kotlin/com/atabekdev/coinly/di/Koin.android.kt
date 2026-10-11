package com.atabekdev.coinly.di

import com.atabekdev.coinly.core.database.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

// Context is registered by androidContext(...) in CoinlyApplication.
actual fun platformModule(): Module = module {
    single { getDatabaseBuilder(get()) }
}
