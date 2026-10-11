package com.atabekdev.coinly.ui.localization

interface StringResources {
    val common: CommonStrings
    val category: CategoryStrings
    val transaction: TransactionStrings
}

class CommonStrings(
    val ok: String,
    val cancel: String,
    val back: String,
    val currency: String,
    // Month names in the genitive case, January first: "11 октября".
    val monthsGenitive: List<String>,
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
