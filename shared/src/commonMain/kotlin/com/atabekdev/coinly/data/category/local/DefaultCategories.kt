package com.atabekdev.coinly.data.category.local

import com.atabekdev.coinly.data.transaction.model.TransactionType
import com.atabekdev.coinly.ui.localization.AppRes

class DefaultCategory(
    val emoji: String,
    val name: String,
    val type: TransactionType,
)

fun defaultCategories(): List<DefaultCategory> {
    val strings = AppRes.strings.category
    return listOf(
        DefaultCategory("🛒", strings.defaultGroceries, TransactionType.EXPENSE),
        DefaultCategory("🍽", strings.defaultCafes, TransactionType.EXPENSE),
        DefaultCategory("🚕", strings.defaultTransport, TransactionType.EXPENSE),
        DefaultCategory("🎬", strings.defaultEntertainment, TransactionType.EXPENSE),
        DefaultCategory("🛍", strings.defaultShopping, TransactionType.EXPENSE),
        DefaultCategory("💊", strings.defaultHealth, TransactionType.EXPENSE),
        DefaultCategory("📱", strings.defaultCommunication, TransactionType.EXPENSE),
        DefaultCategory("🏠", strings.defaultUtilities, TransactionType.EXPENSE),
        DefaultCategory("📦", strings.defaultOtherExpense, TransactionType.EXPENSE),
        DefaultCategory("💼", strings.defaultSalary, TransactionType.INCOME),
        DefaultCategory("🎁", strings.defaultGifts, TransactionType.INCOME),
        DefaultCategory("💰", strings.defaultOtherIncome, TransactionType.INCOME),
    )
}
