package com.atabekdev.coinly.core.database

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<CoinlyDatabase> {
    val appContext = context.applicationContext
    return Room.databaseBuilder<CoinlyDatabase>(
        context = appContext,
        name = appContext.getDatabasePath(DATABASE_NAME).absolutePath,
    )
}
