# Spec Delta

## Purpose

Lets the user record a new income or expense, correct an existing one, or delete it, with input rules and validation that keep every saved transaction complete and sensible.

## ADDED Requirements

### Requirement: New transaction starts with defaults
The "Новая операция" screen SHALL open with the type "Расход", an empty amount, no category chosen, today's date and an empty note.

#### Scenario: Opening the add screen
- **WHEN** the user opens the add screen
- **THEN** "Расход" is selected
- **AND** the amount is empty and no category is highlighted
- **AND** the date field shows today's date, for example "24 октября 2026"
- **AND** the note is empty
- **AND** no delete action is offered

### Requirement: Choose the transaction type
The user SHALL be able to switch between "Расход" and "Доход". The categories offered SHALL be the ones of the chosen type.

#### Scenario: Switch to income
- **WHEN** the user selects "Доход"
- **THEN** only the income categories are offered

#### Scenario: Switch back to expense
- **WHEN** the user selects "Расход"
- **THEN** only the expense categories are offered

### Requirement: Amount input
The amount SHALL be entered in whole sums, with digits only, shown with spaces between thousands and followed by "сум". Other characters SHALL be ignored, leading zeros removed, and input beyond 12 digits ignored.

#### Scenario: Thousands grouping
- **WHEN** the user types 300000
- **THEN** the field shows "300 000"

#### Scenario: Non-digit characters
- **WHEN** the user types a letter or a decimal separator
- **THEN** the amount does not change

#### Scenario: Leading zero
- **WHEN** the user types 0 into an empty amount field
- **THEN** the field stays empty

#### Scenario: Length limit
- **WHEN** the amount already has 12 digits and the user types another digit
- **THEN** the amount does not change

### Requirement: Category choice
The user SHALL choose exactly one category among those of the selected type, shown as chips with the emoji and the name in the stored order. A transaction SHALL NOT be saved without a category of its selected type.

#### Scenario: Choosing a category
- **WHEN** the user taps the chip "🍽 Кафе и рестораны"
- **THEN** that chip is highlighted and any other chip is not

#### Scenario: Category of the other type
- **WHEN** the user chooses an expense category, switches to "Доход" and taps "Сохранить" without choosing an income category
- **THEN** the error "Выберите категорию" is shown and nothing is saved

### Requirement: Date choice
The date SHALL default to today. The user SHALL be able to pick another date from a date picker that allows today and earlier days only, and the choice SHALL be applied only when confirmed with "ОК".

#### Scenario: Pick an earlier date
- **WHEN** the user taps the date field, picks 11 October 2026 and confirms with "ОК"
- **THEN** the date field shows "11 октября 2026"

#### Scenario: Future dates
- **WHEN** the date picker is open
- **THEN** days after today cannot be selected

#### Scenario: Dismissing the picker
- **WHEN** the user taps "Отмена" or dismisses the picker
- **THEN** the date field keeps its previous value

### Requirement: Optional note
The note SHALL be optional and limited to 200 characters, with a counter such as "12/200". Input beyond the limit SHALL be ignored. Spaces at the start and end of the note SHALL be removed when saving.

#### Scenario: Counter
- **WHEN** the user types a 12 character note
- **THEN** the counter reads "12/200"

#### Scenario: Limit reached
- **WHEN** the note has 200 characters and the user types another one
- **THEN** the note does not change

#### Scenario: Surrounding spaces
- **WHEN** the user saves the note "  Такси  "
- **THEN** the saved note is "Такси"

### Requirement: Validation before saving
Tapping "Сохранить" SHALL check the fields in the order amount, category, date. The first failing check SHALL show its message in a dialog and nothing SHALL be saved. The dialog SHALL close with "ОК".

#### Scenario: Empty amount
- **WHEN** the user taps "Сохранить" with an empty amount
- **THEN** a dialog shows "Введите сумму" and nothing is saved

#### Scenario: Amount and category both missing
- **WHEN** the user taps "Сохранить" with an empty amount and no category
- **THEN** only "Введите сумму" is shown

#### Scenario: No category
- **WHEN** the user taps "Сохранить" with a valid amount and no category
- **THEN** a dialog shows "Выберите категорию" and nothing is saved

#### Scenario: Date in the future
- **WHEN** the chosen date is later than today at the moment of saving
- **THEN** a dialog shows "Дата не может быть в будущем" and nothing is saved

#### Scenario: Closing the dialog
- **WHEN** the user taps "ОК" in the error dialog
- **THEN** the dialog closes and the entered values are still there

### Requirement: Save a transaction
A valid form SHALL be saved as a new transaction on the add screen, or as an update of the existing transaction on the edit screen, and the screen SHALL then close and return to the list.

#### Scenario: Save a new expense
- **WHEN** the user enters 45 000, chooses "🚕 Транспорт", keeps today's date and taps "Сохранить"
- **THEN** the screen closes
- **AND** an expense of 45 000 in "Транспорт" dated today is in the list

#### Scenario: Save changes
- **WHEN** the user changes the amount of an existing transaction and taps "Сохранить"
- **THEN** the screen closes
- **AND** the list shows the new amount for that same transaction

### Requirement: Leave without saving
Leaving the screen with the back control SHALL close it and discard the entered values, without asking for confirmation.

#### Scenario: Back from a filled form
- **WHEN** the user has typed an amount and taps "Назад"
- **THEN** the screen closes and no transaction is created

### Requirement: Edit an existing transaction
The "Редактирование" screen SHALL open with the stored type, amount, category, date and note of the chosen transaction, and the user SHALL be able to change any of them, including the type.

#### Scenario: Prefilled form
- **WHEN** the user opens an income of 1 500 000 in "Зарплата" dated 11 October 2026 with the note "Аванс"
- **THEN** "Доход" is selected, the amount shows "1 500 000" and "💼 Зарплата" is highlighted
- **AND** the date shows "11 октября 2026" and the note shows "Аванс"

#### Scenario: Change the type
- **WHEN** the user switches an existing expense to "Доход", chooses an income category and saves
- **THEN** the transaction is stored as an income in that category

### Requirement: Delete a transaction
The edit screen SHALL offer a delete action that asks for confirmation with the title "Удалить операцию?" and the text "Это действие нельзя отменить.". Confirming SHALL remove the transaction and close the screen. The add screen SHALL NOT offer deletion.

#### Scenario: Confirm deletion
- **WHEN** the user taps the delete action and then "Удалить"
- **THEN** the transaction is removed and the screen closes

#### Scenario: Cancel deletion
- **WHEN** the user taps the delete action and then "Отмена"
- **THEN** the dialog closes and the transaction is unchanged

### Requirement: Failures are reported
When saving or deleting fails, the screen SHALL stay open and show a dialog with "Не удалось сохранить операцию" or "Не удалось удалить операцию". When the transaction to edit cannot be found, the screen SHALL show "Операция не найдена".

#### Scenario: Save fails
- **WHEN** the transaction cannot be stored
- **THEN** a dialog shows "Не удалось сохранить операцию" and the screen stays open with its values

#### Scenario: Delete fails
- **WHEN** the transaction cannot be removed
- **THEN** a dialog shows "Не удалось удалить операцию" and the screen stays open

#### Scenario: Transaction not found
- **WHEN** the edit screen is opened for a transaction that no longer exists
- **THEN** a dialog shows "Операция не найдена"

### Requirement: Default categories
On first launch the app SHALL provide these categories, and SHALL NOT add them again once any category exists. Expense: 🛒 Продукты, 🍽 Кафе и рестораны, 🚕 Транспорт, 🎬 Развлечения, 🛍 Покупки, 💊 Здоровье, 📱 Связь и интернет, 🏠 Коммунальные услуги, 📦 Другое. Income: 💼 Зарплата, 🎁 Подарки, 💰 Другое.

#### Scenario: Fresh install
- **WHEN** the user opens the add screen for the first time with "Расход" selected
- **THEN** the nine expense categories are offered in the order above

#### Scenario: Income categories
- **WHEN** the user selects "Доход" on a fresh install
- **THEN** "💼 Зарплата", "🎁 Подарки" and "💰 Другое" are offered

#### Scenario: Existing categories are kept
- **WHEN** the app starts and categories already exist
- **THEN** no default category is added
