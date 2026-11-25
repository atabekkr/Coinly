# Tasks

This change adds no code. Each task checks the running app against the specs. When the app and a
spec disagree, do not change the app: fix the spec if it misstated the behavior, otherwise add the
difference to the out-of-scope notes in `proposal.md`.

Run the app on an Android emulator or device. The iOS 26.3 simulator draws every emoji as a "?" box,
so emoji scenarios cannot be judged there.

## 1. Fresh-install baseline

- [x] 1.1 Uninstall the app, install it again and open it; verify it starts on the "Главная" tab and the "Операции" tab opens with an empty month
- [x] 1.2 Open the add screen and check the "Default categories" scenarios in `transaction-edit`: nine expense chips in the listed order, then "Доход" shows the three income chips; verify all match the spec
- [x] 1.3 Close and reopen the app, open the add screen again; verify no category appears twice ("Existing categories are kept")

## 2. Add screen

- [x] 2.1 Check "New transaction starts with defaults" and "Choose the transaction type"; verify "Расход" is selected, the amount and note are empty, no chip is highlighted, today's date is shown, there is no delete action, and switching the type swaps the chips
- [x] 2.2 Check "Amount input": type 300000, a letter, a decimal separator, a leading 0, and a thirteenth digit; verify each scenario result
- [x] 2.3 Check "Category choice" and "Date choice": tap chips, pick 11 of the current month with "ОК", then reopen the picker and use "Отмена"; verify only one chip is highlighted, the chosen date shows as "11 октября 2026" style, cancelling keeps the date, and days after today cannot be selected
- [x] 2.4 Check "Optional note": type 12 characters, then paste 200+ characters, then save a note with spaces around it; verify the counter reads "12/200", input stops at 200, and the saved note has no surrounding spaces
- [x] 2.5 Check "Validation before saving": tap "Сохранить" with nothing filled, with only an amount, and with a valid form after switching the type without choosing a new category; verify the dialogs read "Введите сумму" and "Выберите категорию", the first failing check wins, and "ОК" keeps the entered values
- [x] 2.6 Check the "Date in the future" validation scenario: open the add screen, move the device clock back one day, tap "Сохранить" with a valid form; verify "Дата не может быть в будущем" appears, then restore the clock. If the clock cannot be moved, mark the scenario as unchecked in 5.1
- [x] 2.7 Check "Save a transaction" and "Leave without saving": save a new expense, and on another attempt type an amount and tap "Назад"; verify the screen closes both times, the first transaction is in the list and the second does not exist

## 3. Edit and delete

- [x] 3.1 Check "Edit an existing transaction": open an income with a note, then change an expense into an income with another category and save; verify the form is prefilled with the stored type, amount, category, date and note, and the saved transaction is an income
- [x] 3.2 Check "Delete a transaction": open an existing transaction, tap delete, cancel, then delete and confirm; verify the dialog title and text match the spec, cancelling changes nothing, confirming removes the transaction and closes the screen
- [x] 3.3 Check "Failures are reported": these three scenarios cannot be provoked from the UI. Read the save, delete and load paths in `TransactionEditScreenModel` and the dialog in `TransactionEditScreen`; verify the messages "Не удалось сохранить операцию", "Не удалось удалить операцию" and "Операция не найдена" are the ones shown, and mark the scenarios as confirmed by reading in 5.1

## 4. Transaction list

- [ ] 4.1 Create test data through the add screen, adjusting dates if today is the 1st or 2nd of the month: today an expense of 45 000 in "Транспорт" with the note "Такси домой" and then an expense of 12 000 in "Продукты" without a note; yesterday an income of 1 500 000 in "Зарплата"; the 11th an expense with a 200 character note; one expense in the previous month; leave the month before that empty; verify all appear in the list
- [ ] 4.2 Check "List opens on the current month" and "Navigate between months": reopen the tab, go back one month, forward again, and try to go past the current month; verify the heading, transactions and totals follow the month and "Следующий месяц" is disabled on the current month
- [ ] 4.3 Check "Transactions are grouped by day" and "Day headings"; verify the newest day is first, the 12 000 expense is above the 45 000 one, days without transactions have no heading, and the headings read "Сегодня", "Вчера" and "11 октября"
- [ ] 4.4 Check "Transaction row content" and "Month totals"; verify emoji, category, one-line note, "+" in the income color, "−" in the normal color and the "сум" suffix on rows, and that the totals "Доходы" and "Расходы" equal the sums of the test data with a zero total shown as "0 сум"
- [ ] 4.5 Check "Empty month": open the month with no transactions; verify "Нет операций за этот месяц" and both totals reading "0 сум"
- [ ] 4.6 Check "Open the add and edit screens from the list" and "List reflects changes immediately": open both screens from the list, add a transaction, delete the only one of a day, and change a date into another month; verify the screens cover the tab bar and the list and totals update without any refresh action

## 5. Close out

- [ ] 5.1 Go through every scenario of both specs; verify each one is confirmed on the device, confirmed by reading (3.3), or listed with a decision to fix the spec wording or to note the app difference in `proposal.md`
- [ ] 5.2 Run `openspec validate "transactions-mvp" --strict`; verify it reports the change as valid after any spec edits

## Workflow follow-up

- Archive the change so the two capabilities move into `openspec/specs/`.
- Verify `openspec list --specs` shows `transaction-list` and `transaction-edit`.
