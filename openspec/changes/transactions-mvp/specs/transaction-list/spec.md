# Spec Delta

## Purpose

Lets the user see what they earned and spent in one calendar month, grouped by day, with the month's income and expense totals, and move between months to review the past.

## ADDED Requirements

### Requirement: List opens on the current month
The transaction list SHALL show the current calendar month when the user opens it.

#### Scenario: First view
- **WHEN** the user opens the "Операции" tab
- **THEN** the month heading shows the current month and year, for example "Октябрь 2026"
- **AND** the transactions and totals shown belong to that month only

### Requirement: Navigate between months
The user SHALL be able to move to the previous or next calendar month. The list SHALL NOT open any month after the current one.

#### Scenario: Go to an earlier month
- **WHEN** the user taps "Предыдущий месяц"
- **THEN** the heading, the transactions and the totals switch to the previous month

#### Scenario: Return toward the present
- **WHEN** the user is on a past month and taps "Следующий месяц"
- **THEN** the list switches to the following month

#### Scenario: Current month is the last one
- **WHEN** the displayed month is the current month
- **THEN** the "Следующий месяц" control is disabled
- **AND** the list stays on the current month

### Requirement: Transactions are grouped by day
The list SHALL group the month's transactions under one heading per day, with the newest day first and, inside a day, the most recently created transaction first. Days without transactions SHALL NOT appear.

#### Scenario: Two days
- **WHEN** the month has transactions on the 3rd and on the 10th
- **THEN** the 10th appears above the 3rd
- **AND** each transaction appears under the heading of its own day

#### Scenario: Same day
- **WHEN** two transactions share a date and one was created after the other
- **THEN** the one created later is listed first

#### Scenario: Day without transactions
- **WHEN** no transaction has the date of the 5th
- **THEN** no heading for the 5th is shown

### Requirement: Day headings
Each day heading SHALL read "Сегодня" for today, "Вчера" for yesterday, and the day with the month name in the genitive case, without the year, for any other date.

#### Scenario: Today and yesterday
- **WHEN** the month contains transactions dated today and yesterday
- **THEN** their headings read "Сегодня" and "Вчера"

#### Scenario: Older day
- **WHEN** a transaction is dated 11 October and that date is neither today nor yesterday
- **THEN** its heading reads "11 октября"

### Requirement: Transaction row content
Each transaction row SHALL show the category emoji, the category name, the note when it is not blank, and the amount. The amount SHALL carry a "+" and the income color for income, and a "−" in the normal text color for expenses, followed by the currency "сум". Amounts SHALL group thousands with spaces.

#### Scenario: Expense with a note
- **WHEN** an expense of 45 000 in "Транспорт" has the note "Такси домой"
- **THEN** the row shows "🚕", "Транспорт", the note "Такси домой" and "−45 000 сум"

#### Scenario: Income without a note
- **WHEN** an income of 1 500 000 in "Зарплата" has an empty note
- **THEN** the row shows "💼" and "Зарплата" with no note line
- **AND** the amount reads "+1 500 000 сум" in the income color

#### Scenario: Long note
- **WHEN** a note does not fit on one line
- **THEN** only one line of it is shown

### Requirement: Month totals
The list SHALL show the total income and the total expenses of the displayed month, labelled "Доходы" and "Расходы", using the same sign and currency format as the rows. A total of zero SHALL be shown without a sign.

#### Scenario: Month with both kinds
- **WHEN** the month has income of 2 000 000 and expenses of 350 000 in total
- **THEN** the totals read "+2 000 000 сум" and "−350 000 сум"

#### Scenario: Nothing of one kind
- **WHEN** the month has expenses but no income
- **THEN** the income total reads "0 сум"

### Requirement: Empty month
When the displayed month has no transactions, the list SHALL show the message "Нет операций за этот месяц" and both totals SHALL read zero.

#### Scenario: Month with no transactions
- **WHEN** the user opens a month that has no transactions
- **THEN** the message "Нет операций за этот месяц" is shown
- **AND** the totals read "0 сум" and "0 сум"

### Requirement: Open the add and edit screens from the list
The list SHALL offer a way to add a transaction and a way to open an existing one for editing. The add / edit screen SHALL open over the tab bar.

#### Scenario: Add
- **WHEN** the user taps the "Добавить операцию" button
- **THEN** the "Новая операция" screen opens above the tabs

#### Scenario: Edit
- **WHEN** the user taps a transaction row
- **THEN** the "Редактирование" screen opens for that transaction above the tabs

### Requirement: List reflects changes immediately
The list and the totals SHALL update on their own after a transaction is added, changed or deleted, without any manual refresh.

#### Scenario: After adding
- **WHEN** the user saves a new expense dated today and returns to the list on the current month
- **THEN** the transaction appears under "Сегодня"
- **AND** the expense total includes its amount

#### Scenario: After deleting
- **WHEN** the user deletes the only transaction of a day and returns to the list
- **THEN** that day's heading is gone
- **AND** the totals no longer include its amount

#### Scenario: Moved to another month
- **WHEN** the user changes a transaction's date to a date in a different month
- **THEN** it disappears from the displayed month and is counted in the other month
