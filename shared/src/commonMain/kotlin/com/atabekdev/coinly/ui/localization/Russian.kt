package com.atabekdev.coinly.ui.localization

object Russian : StringResources {

    override val common = CommonStrings(
        ok = "ОК",
        cancel = "Отмена",
        back = "Назад",
        currency = "сум",
        monthsNominative = listOf(
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь",
        ),
        monthsGenitive = listOf(
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря",
        ),
    )

    override val home = HomeStrings(
        title = "Главная",
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
        listTitle = "Операции",
        add = "Добавить операцию",
        previousMonth = "Предыдущий месяц",
        nextMonth = "Следующий месяц",
        totalIncome = "Доходы",
        totalExpense = "Расходы",
        emptyMonth = "Нет операций за этот месяц",
        today = "Сегодня",
        yesterday = "Вчера",
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
