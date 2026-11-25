# Spec Delta

## Purpose

Lets the user see on the Home tab where the current month's money went: a ring divided by color into expense categories, and a list under it with each category's amount and share.

## ADDED Requirements

### Requirement: Home shows the current month's expense breakdown
The Home tab SHALL show the expenses of the current calendar month split by category. The heading SHALL read "Расходы по категориям" with the month and year, for example "Октябрь 2026". There SHALL be no control to change the month.

#### Scenario: First view
- **WHEN** the user opens the "Главная" tab and the current month has expenses
- **THEN** the heading "Расходы по категориям" and the current month, for example "Октябрь 2026", are shown
- **AND** the ring and the category list below it describe that month only

#### Scenario: Other months are not counted
- **WHEN** an expense is dated in a month other than the current one
- **THEN** it is not part of any slice, row or total on the Home tab

### Requirement: Slices are sized by share of expenses
The ring SHALL be divided into one slice per category with expenses in the month. Each slice's size SHALL be proportional to that category's share of the month's total expenses. Income SHALL NOT appear in the ring or in the list.

#### Scenario: Shares of the ring
- **WHEN** the month's expenses are "Транспорт" 620 000, "Продукты" 310 000, "Кафе и рестораны" 186 000 and "Другое" 124 000
- **THEN** the ring has four slices taking half, a quarter, 15% and 10% of the circle

#### Scenario: Income is ignored
- **WHEN** the month also has an income of 1 500 000 in "Зарплата"
- **THEN** "Зарплата" has no slice and no row, and the shares of the expense categories do not change

#### Scenario: Category without expenses
- **WHEN** no expense of the month is in "Здоровье"
- **THEN** "Здоровье" has no slice and no row

### Requirement: Order of slices and rows
Slices and rows SHALL be ordered by amount spent, biggest first. The first slice SHALL start at the top of the ring (12 o'clock) and the slices SHALL follow clockwise in the same order as the rows.

#### Scenario: Biggest first
- **WHEN** "Продукты" has the largest amount in the month
- **THEN** its slice is the first one clockwise from the top
- **AND** its row is the first row of the list

#### Scenario: Equal amounts
- **WHEN** two categories have exactly the same amount
- **THEN** they appear in the same relative order every time the Home tab is shown

### Requirement: Category colors
Each category SHALL always be drawn in the same color, in the ring and in its list row. The color SHALL depend only on the category, not on its share, its rank in the month or which other categories have expenses. The nine default expense categories SHALL have different colors from each other. When there are more categories than distinct colors, colors MAY repeat.

#### Scenario: Slice and row match
- **WHEN** "Транспорт" has a slice in the ring
- **THEN** the dot in the "Транспорт" row has the same color as that slice

#### Scenario: Color does not change with the data
- **WHEN** "Транспорт" is the biggest slice and the user then adds an expense that makes "Продукты" the biggest
- **THEN** "Транспорт" keeps the color it had before

#### Scenario: Default categories are told apart
- **WHEN** the month has expenses in all nine default expense categories
- **THEN** all nine slices have different colors

### Requirement: Month total in the ring
The center of the ring SHALL show the label "Всего" and the month's total expenses in the normal text color, without a sign, thousands grouped by spaces and followed by "сум". The total SHALL equal the sum of the amounts in the list.

#### Scenario: Total
- **WHEN** the month's expenses are 620 000, 310 000, 186 000 and 124 000 in four categories
- **THEN** the center shows "Всего" and "1 240 000 сум"

### Requirement: Category list
Under the ring, the Home tab SHALL list every category in the ring. A row SHALL show a color dot, the category emoji, the category name, the amount spent without a sign and followed by "сум", and the percent share. The list SHALL be reachable by scrolling when it does not fit on the screen.

#### Scenario: Row content
- **WHEN** "Транспорт" has 620 000 of 1 240 000 in the month
- **THEN** its row shows a dot in the slice color, "🚕", "Транспорт", "620 000 сум" and "50%"

#### Scenario: Many categories
- **WHEN** the month has expenses in all nine default expense categories and the rows do not fit on the screen
- **THEN** the user can scroll the Home tab to see every row

### Requirement: Percent shares
A category's percent SHALL be its share of the month's total expenses rounded to the nearest whole percent. A share below 1% SHALL read "<1%". The percents of different rows MAY not add up to exactly 100%.

#### Scenario: Whole percents
- **WHEN** the month's expenses are 620 000, 310 000, 186 000 and 124 000
- **THEN** the rows read "50%", "25%", "15%" and "10%"

#### Scenario: Small share
- **WHEN** "Продукты" has 600 000, "Транспорт" 394 000 and "Кафе и рестораны" 6 000 in the month
- **THEN** the rows read "60%", "39%" and "<1%"
- **AND** "Кафе и рестораны" still has a visible slice

### Requirement: Single category
When all of the month's expenses are in one category, the ring SHALL be one full circle in that category's color, without a gap, and its row SHALL read "100%".

#### Scenario: One category only
- **WHEN** the month's only expenses are in "Транспорт"
- **THEN** the ring is a complete circle in the "Транспорт" color
- **AND** the list has one row reading "100%"

### Requirement: Month without expenses
When the current month has no expenses, the Home tab SHALL show the message "Нет расходов за этот месяц" instead of the ring and the list. A month that has only income SHALL count as having no expenses.

#### Scenario: No transactions
- **WHEN** the current month has no transactions
- **THEN** "Нет расходов за этот месяц" is shown
- **AND** no ring and no category row are shown

#### Scenario: Only income
- **WHEN** the current month has an income but no expense
- **THEN** "Нет расходов за этот месяц" is shown

### Requirement: Breakdown reflects changes immediately
The ring, the center total and the list SHALL update on their own after an expense is added, changed or deleted, without any manual refresh.

#### Scenario: After adding an expense
- **WHEN** the user saves a new expense dated this month in "Продукты" and returns to the "Главная" tab
- **THEN** the "Продукты" slice, its row, the percents and the center total include the new amount

#### Scenario: After deleting the only expense
- **WHEN** the user deletes the only expense of the month and returns to the "Главная" tab
- **THEN** "Нет расходов за этот месяц" is shown

#### Scenario: Moved to another month
- **WHEN** the user changes an expense's date to a date in a different month
- **THEN** its amount is no longer part of the Home tab's ring, list or total

#### Scenario: Adding an income
- **WHEN** the user saves an income dated this month
- **THEN** the ring, the list and the total do not change
