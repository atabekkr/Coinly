package com.atabekdev.coinly

import android.app.Application
import com.atabekdev.coinly.di.initKoin
import org.koin.android.ext.koin.androidContext

class CoinlyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@CoinlyApplication)
        }
    }
}
