package com.atabekdev.coinly.ui.localization

object Russian : StringResources {

    override val common = CommonStrings(
        ok = "ОК",
        cancel = "Отмена",
        back = "Назад",
        currency = "сум",
        monthsGenitive = listOf(
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря",
        ),
    )

    override val category = CategoryStrings(
        defaultGroceries = "Продукты",
        defaultCafes = "Кафе и рестораны",
        defaultTransport = "Транспорт",
        defaultEntertainment = "Развлечения",
        defaultShopping = "Покупки",
        defaultHealth = "Здоровье",
        defaultCommunication = "Связь и интернет",
        defaultUtilities = "Коммунальные услуги",
        defaultOtherExpense = "Другое",
        defaultSalary = "Зарплата",
        defaultGifts = "Подарки",
        defaultOtherIncome = "Другое",
    )

    override val transaction = TransactionStrings(
        newTitle = "Новая операция",
        editTitle = "Редактирование",
        expense = "Расход",
        income = "Доход",
        amount = "Сумма",
        category = "Категория",
        date = "Дата",
        selectDate = "Выбрать дату",
        note = "Комментарий",
        save = "Сохранить",
        delete = "Удалить",
        deleteTitle = "Удалить операцию?",
        deleteMessage = "Это действие нельзя отменить.",
        errorEmptyAmount = "Введите сумму",
        errorNoCategory = "Выберите категорию",
        errorFutureDate = "Дата не может быть в будущем",
        errorNotFound = "Операция не найдена",
        errorSave = "Не удалось сохранить операцию",
        errorDelete = "Не удалось удалить операцию",
    )
}
