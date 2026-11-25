package com.atabekdev.coinly.ui.localization

object Russian : StringResources {

    override val common = CommonStrings(
        ok = "ОК",
        cancel = "Отмена",
        back = "Назад",
        close = "Закрыть",
        currency = "сум",
        inDevelopment = "В разработке",
        inDevelopmentHint = "Этот раздел скоро появится",
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
        profile = "Профиль",
        notifications = "Уведомления",
        previousMonth = "Предыдущий месяц",
        nextMonth = "Следующий месяц",
        totalBalance = "Общий баланс",
        thisMonth = "за месяц",
        income = "Доходы",
        expenses = "Расходы",
        transactions = "Операции",
        seeAll = "Все",
        transactionsEmpty = "Нет операций за этот месяц",
        today = "Сегодня",
        yesterday = "Вчера",
    )

    override val report = ReportStrings(
        title = "Отчёт",
        previousMonth = "Предыдущий месяц",
        nextMonth = "Следующий месяц",
        expenses = "Расходы",
        income = "Доходы",
        total = "Всего",
        dailyAverage = "В среднем за день",
        topCategory = "Топ-категория",
        byCategory = "По категориям",
        expensesEmpty = "Нет расходов за этот месяц",
        incomeEmpty = "Нет доходов за этот месяц",
        lessThanOnePercent = "<1%",
    )

    override val budget = BudgetStrings(
        title = "Бюджеты",
        add = "Добавить лимит",
        monthBudget = "Бюджет на",
        leftOf = "осталось из",
        spent = "потрачено",
        daysLeft = "Осталось дней:",
        categories = "Категории",
        of = "из",
        left = "осталось",
        overBy = "превышен на",
        empty = "Лимиты не заданы",
        emptyHint = "Нажмите «+», чтобы задать месячный лимит для категории расходов",
        newTitle = "Новый лимит",
        editTitle = "Лимит",
        allBudgeted = "Для всех категорий расходов лимит уже задан",
        deleteDigit = "Стереть цифру",
        save = "Сохранить",
        delete = "Удалить",
        deleteTitle = "Удалить лимит?",
        deleteMessage = "Операции в этой категории останутся.",
        errorEmptyAmount = "Введите сумму лимита",
        errorNoCategory = "Выберите категорию",
        errorNotFound = "Лимит не найден",
        errorSave = "Не удалось сохранить лимит",
        errorDelete = "Не удалось удалить лимит",
    )

    override val settings = SettingsStrings(
        title = "Настройки",
        version = "Coinly, версия 1.0",
    )

    override val profile = ProfileStrings(
        title = "Профиль",
    )

    override val notification = NotificationStrings(
        title = "Уведомления",
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
        deleteDigit = "Стереть цифру",
        category = "Категория",
        date = "Дата",
        selectDate = "Выбрать дату",
        note = "Комментарий",
        save = "Сохранить",
        saveExpense = "Сохранить расход",
        saveIncome = "Сохранить доход",
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
