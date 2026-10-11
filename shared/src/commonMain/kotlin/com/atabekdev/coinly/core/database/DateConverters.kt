package com.atabekdev.coinly.core.database

import androidx.room3.ColumnTypeConverter
import kotlinx.datetime.LocalDate

class DateConverters {

    @ColumnTypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @ColumnTypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)
}
