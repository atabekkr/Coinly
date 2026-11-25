# Proposal

## Why

The "Главная" tab is an empty placeholder that shows only the word "Coinly". The first thing a
person wants from a finance app's home screen is to see where the month's money went. The
transaction list shows every expense, but it does not show which categories take the biggest share.

## What Changes

- The Home tab shows an expense breakdown for the **current calendar month**: a donut ring divided
  into one colored slice per expense category, sized by that category's share of the month's
  expenses, with the month's total expense in the center.
- Below the ring, a list of the same categories, biggest first. Each row shows a color dot matching
  its slice, the category emoji and name, the amount spent and the percent share. The list doubles
  as the legend.
- Each category keeps the same color every time. The color comes from a fixed palette, picked by the
  category's existing `sortOrder`. No schema change. Colors repeat once the palette runs out.
- A month without expenses shows a short message instead of a ring and a list.
- Data comes from one new SQL aggregate (expenses of a month, summed per category, joined with the
  category), observed as a `Flow` so the chart updates on its own after any transaction change. It
  takes a `YearMonth`, like the transaction list's queries, so a later Statistics screen can reuse
  it.
- Out of scope: a month switcher, income breakdown, tapping or highlighting a slice, animation,
  balance and income / expense cards, limits, recent transactions, stored or user-chosen category
  colors. The known issue that "the current month is computed once and goes stale after midnight"
  (noted in `transactions-mvp`) is inherited here and not fixed.

## Capabilities

### New Capabilities

- `home-expense-breakdown`: the Home tab's view of the current month's expenses split by category,
  as a donut ring with a legend list, including colors, percents and the empty state.

### Modified Capabilities

None. `openspec/specs/` is empty; the transaction specs are still in the unarchived
`transactions-mvp` change and this change does not alter what they describe.

## Impact

- Code, data layer: a new query in `TransactionDao`, a new result class next to `TransactionTotal`,
  a new model in `data/transaction/model/`, and a new method in `TransactionRepository`.
- Code, Home screen: `HomeState`, `HomeScreenModel` and `HomeScreen` change; `HomeEvent` and the
  Koin registration stay as they are.
- Code, shared UI: a palette in `ui/theme/Color.kt`; new entries in `HomeStrings`
  (`StringResources.kt`) and `Russian.kt`.
- Database schema: none. No version bump, no new files in `shared/schemas/`.
- Dependencies, `build.gradle.kts`, Koin startup, root navigation: none touched. The ring is drawn
  with Compose `Canvas`.
- About nine files across three layers, which is why this is planned before any code.
