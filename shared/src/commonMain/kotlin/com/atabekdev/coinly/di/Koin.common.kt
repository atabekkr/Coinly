package com.atabekdev.coinly.di

import com.atabekdev.coinly.screen.home.HomeScreenModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(screenModelModule())
    }
}

fun screenModelModule() = module {
    factory { HomeScreenModel() }
}
