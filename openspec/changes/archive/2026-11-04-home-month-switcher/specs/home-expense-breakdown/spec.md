# Spec Delta

## ADDED Requirements

### Requirement: Navigate between months
The user SHALL be able to move the Home tab to the previous or the next calendar month with an arrow on each side of the displayed month and year, named "Предыдущий месяц" and "Следующий месяц". The tab SHALL NOT open any month after the current one. There is no limit on going back.

#### Scenario: Go to an earlier month
- **WHEN** the displayed month is "Октябрь 2026" and the user taps "Предыдущий месяц"
- **THEN** the month reads "Сентябрь 2026"
- **AND** the ring, the center total and the list describe September 2026 only

#### Scenario: Return toward the present
- **WHEN** the user is on a past month and taps "Следующий месяц"
- **THEN** the Home tab switches to the following month

#### Scenario: Current month is the last one
- **WHEN** the displayed month is the current month
- **THEN** the "Следующий месяц" control is disabled
- **AND** the Home tab stays on the current month

#### Scenario: Going back with no data
- **WHEN** the user goes back to a month before the first transaction
- **THEN** "Нет расходов за этот месяц" is shown
- **AND** the user can keep going back or return to a later month

#### Scenario: The choice is not remembered
- **WHEN** the user views an earlier month, closes the app and opens it again
- **THEN** the Home tab shows the current month

## MODIFIED Requirements

### Requirement: Home shows the expense breakdown of one month
The Home tab SHALL show the expenses of one calendar month split by category and SHALL open on the current month. The heading SHALL read "Расходы по категориям". The displayed month and year, for example "Октябрь 2026", SHALL be shown under it, between the controls for the previous and the next month.

#### Scenario: First view
- **WHEN** the user opens the "Главная" tab and the current month has expenses
- **THEN** the heading "Расходы по категориям" and the current month, for example "Октябрь 2026", are shown
- **AND** the ring and the category list below it describe that month only

#### Scenario: Other months are not counted
- **WHEN** an expense is dated in a month other than the displayed one
- **THEN** it is not part of any slice, row or total on the Home tab

### Requirement: Month without expenses
When the displayed month has no expenses, the Home tab SHALL show the message "Нет расходов за этот месяц" instead of the ring and the list. A month that has only income SHALL count as having no expenses. The heading and the month controls SHALL stay visible.

#### Scenario: No transactions
- **WHEN** the displayed month has no transactions
- **THEN** "Нет расходов за этот месяц" is shown
- **AND** no ring and no category row are shown
- **AND** the heading, the month and the month controls are still shown

#### Scenario: Only income
- **WHEN** the displayed month has an income but no expense
- **THEN** "Нет расходов за этот месяц" is shown

### Requirement: Breakdown reflects changes immediately
The ring, the center total and the list SHALL update on their own after an expense of the displayed month is added, changed or deleted, without any manual refresh.

#### Scenario: After adding an expense
- **WHEN** the user saves a new expense dated in the displayed month in "Продукты" and returns to the "Главная" tab
- **THEN** the "Продукты" slice, its row, the percents and the center total include the new amount

#### Scenario: After deleting the only expense
- **WHEN** the user deletes the only expense of the displayed month and returns to the "Главная" tab
- **THEN** "Нет расходов за этот месяц" is shown

#### Scenario: Moved to another month
- **WHEN** the user changes the date of an expense of the displayed month to a date in a different month
- **THEN** its amount is no longer part of the Home tab's ring, list or total

#### Scenario: Adding an income
- **WHEN** the user saves an income dated in the displayed month
- **THEN** the ring, the list and the total do not change

## RENAMED Requirements

- FROM: `### Requirement: Home shows the current month's expense breakdown`
- TO: `### Requirement: Home shows the expense breakdown of one month`
