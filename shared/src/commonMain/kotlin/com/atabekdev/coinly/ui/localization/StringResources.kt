package com.atabekdev.coinly.ui.localization

interface StringResources {
    val common: CommonStrings
    val home: HomeStrings
    val category: CategoryStrings
    val transaction: TransactionStrings
}

class CommonStrings(
    val ok: String,
    val cancel: String,
    val back: String,
    val currency: String,
    // Month names, January first. Nominative: "Октябрь 2026"; genitive: "11 октября".
    val monthsNominative: List<String>,
    val monthsGenitive: List<String>,
)

class HomeStrings(
    val title: String,
)

class CategoryStrings(
    val defaultGroceries: String,
    val defaultCafes: String,
    val defaultTransport: String,
    val defaultEntertainment: String,
    val defaultShopping: String,
    val defaultHealth: String,
    val defaultCommunication: String,
    val defaultUtilities: String,
    val defaultOtherExpense: String,
    val defaultSalary: String,
    val defaultGifts: String,
    val defaultOtherIncome: String,
)

class TransactionStrings(
    val listTitle: String,
    val add: String,
    val previousMonth: String,
    val nextMonth: String,
    val totalIncome: String,
    val totalExpense: String,
    val emptyMonth: String,
    val today: String,
    val yesterday: String,
    val newTitle: String,
    val editTitle: String,
    val expense: String,
    val income: String,
    val amount: String,
    val category: String,
    val date: String,
    val selectDate: String,
    val note: String,
    val save: String,
    val delete: String,
    val deleteTitle: String,
    val deleteMessage: String,
    val errorEmptyAmount: String,
    val errorNoCategory: String,
    val errorFutureDate: String,
    val errorNotFound: String,
    val errorSave: String,
    val errorDelete: String,
)
