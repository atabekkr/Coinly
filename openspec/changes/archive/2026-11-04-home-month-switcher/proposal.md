# Proposal

## Why

The Home tab's expense breakdown is fixed to the current calendar month. The person cannot see where
last month's money went, which is the natural next question once the ring shows this month. The
transaction list already lets them move between months, so Home is the odd one out. The earlier
change `home-expense-chart` left this out of scope on purpose and said it would come later.

## What Changes

- The Home tab gets previous and next month controls. The month line under the heading
  "Расходы по категориям" becomes a row with an arrow on each side of the month and year, for
  example "‹  Октябрь 2026  ›", the same as the header of the "Операции" tab.
- The ring, the center total and the category list show the displayed month. The tab still opens on
  the current month.
- Going back has no limit. The "next" arrow is disabled on the current month and no month after it
  can be opened, because future dates cannot be picked, so such a month is always empty (the same
  rule as the transaction list).
- A month without expenses shows the existing message "Нет расходов за этот месяц", also for past
  months.
- The displayed month is not saved. Closing and reopening the app returns Home to the current month.
- The ring and list update as before when a transaction changes. They follow the displayed month, so
  an expense dated in another month does not touch them.
- Out of scope: swiping between months, a month or year picker, remembering the chosen month,
  limiting how far back the user can go, and the known issue that "the current month is computed once
  and goes stale after midnight" (noted in `transactions-mvp`, inherited here).

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `home-expense-breakdown`: the breakdown is no longer pinned to the current month. The requirement
  that says "current calendar month" and "no control to change the month" is renamed and reworded;
  a requirement for moving between months is added; "Month without expenses" and "Breakdown
  reflects changes immediately" are reworded from "current / this month" to "the displayed month".

## Impact

- Code, Home screen: `HomeEvent` (two events), `HomeState` (a `currentMonth` field), `HomeScreenModel`
  (observes the chosen month and restarts the observation when it changes) and `HomeScreen` (the
  header row).
- Code, strings: two new entries in `HomeStrings` (`StringResources.kt`) and `Russian.kt` for the
  arrows' accessibility labels. The "Операции" strings are not reused.
- Data layer: none. `TransactionRepository.observeMonthExpensesByCategory` already takes a
  `YearMonth`.
- Database schema: none. No version bump, no new files in `shared/schemas/`.
- Dependencies, `build.gradle.kts`, Koin startup, root navigation: none touched. The arrow icons
  `ic_chevron_left` and `ic_chevron_right` already exist.
- Five files in one layer (the screen) plus its strings, which is why this is planned before any
  code.
