# Design

## Context

- `HomeState` already has `month: YearMonth` (no default). `HomeScreenModel` builds the initial
  state from a private `currentMonth()` at the bottom of its file, starts
  `observeExpenses(state.value.month)` in `init`, and the collector updates only `categories` and
  `total` with `mutableState.update { it.copy(...) }`. Nothing ever changes `month` today.
  `HomeEvent` has only `Idle`; `setState` has only `loading`.
- `TransactionRepository.observeMonthExpensesByCategory(month: YearMonth)` already takes the month
  and passes `month.firstDay` / `month.lastDay` to the DAO. The data layer needs nothing.
- The closest existing code is the "Операции" tab. `TransactionListState` has `month` and
  `currentMonth`; `TransactionListScreenModel` keeps an `observeJob`, `changeMonth(month)` calls
  `setState(month = month)` and restarts the observation, and the next-month event is accepted only
  when `next <= currentMonth`. `TransactionListScreen` draws the header as a `Row`: an `IconButton`
  with `ic_chevron_left`, the month text with `weight(1f)`, an `IconButton` with `ic_chevron_right`
  and `enabled = state.month < state.currentMonth`. That row is a `LazyColumn` item.
- `HomeScreen` draws its header as the first `LazyColumn` item: the heading `expensesTitle` and,
  under it, `formatMonth(state.month)` in `bodyMedium` / `onSurfaceVariant`.
- The month arithmetic exists in `kotlinx.datetime` (`minusMonth`, `plusMonth`), already used by
  the list model. `ic_chevron_left` and `ic_chevron_right` are already in `composeResources/drawable/`.
- `HomeScreen` is an `object` tab whose model comes from `koinScreenModel`, registered as a
  `factory`. No Koin change is needed.

See `proposal.md` for the motivation.

## Goals / Non-Goals

**Goals:**
- Reuse the list screen's month switching as closely as possible, so the two tabs behave the same
  and no new pattern appears.
- No change to the data layer, the schema, Koin, navigation or build files.

**Non-Goals:**
- Extracting a shared month-switcher composable or helper from the list screen (needs approval).
- Swiping, a month picker, remembering the month, a limit on how far back one can go.
- Fixing the stale "current month" after midnight (inherited, tracked in `transactions-mvp`).

## Decisions

**1. State: add `currentMonth`, like the list state.**
`HomeState` gains `currentMonth: YearMonth` with no default. The initial state is built once from
the current date, `currentMonth().let { HomeState(month = it, currentMonth = it) }`, as in
`TransactionListScreenModel`. The screen needs it to disable the "next" arrow, and the ScreenModel
needs it for the guard in decision 2.
*Alternative:* compare against the clock inside the composable. Rejected: the list does not, and
state is easier to reason about than a clock read during composition.

**2. Events: two clicks, the guard lives in the ScreenModel.**
`HomeEvent` gains `OnPreviousMonthClick` and `OnNextMonthClick`; `Idle` stays. Previous always calls
`changeMonth(state.value.month.minusMonth())`. Next calls `changeMonth(next)` only when
`next <= state.value.currentMonth`, so a future month cannot be opened even if the arrow is somehow
tapped while disabled. The submit-style "event with no data" rule holds: neither event carries a
value.

**3. `setState` gets `month`, not `currentMonth`.**
`setState(month: YearMonth? = null, loading: Boolean = false)` keeps `month = month ?: it.month`
(persistent data defaults to `?: it.field`) and resets `isLoading` as before. `currentMonth` is set
once and never changes, so it is not a `setState` parameter, which is how
`TransactionListScreenModel.setState` treats it. This is a small, deliberate difference from the
literal CLAUDE.md sentence "a new State field must also be added to `setState`", and it follows the
existing list model.

**4. Restart the observation when the month changes.**
`HomeScreenModel` keeps `private var observeJob: Job?`. `observeExpenses(month)` cancels it, then
`observeJob = screenModelScope.launch { ...collect... }` with the same collector body as today
(update only `categories` and `total` through `mutableState.update`; never touch `isLoading` or
`month`). `changeMonth(month)` is `setState(month = month)` followed by `observeExpenses(month)`.
The initial `init` call goes through the same function. The cancel and the relaunch run on the
ScreenModel's own scope, so a value of the old month already queued cannot land after the new
observation starts.
*Alternative:* one long-lived collector over `state.map { it.month }.distinctUntilChanged()
.flatMapLatest { ... }`. Rejected: it rewrites the working collector and differs from the list model.

**5. Keep the old chart until the new month's data arrives.**
Nothing is cleared on a switch. `categories` and `total` keep the previous month until Room emits
the new one, which takes a moment on a local database.
*Alternative A:* reset `categories` to empty first. Rejected: it flashes "Нет расходов за этот месяц"
on every tap.
*Alternative B:* set `isLoading = true`. Rejected: the existing spinner branch replaces the whole
content, so the arrows would vanish and reappear on every tap.

**6. Header: heading, then the month row copied from the list.**
In the first `LazyColumn` item the heading `expensesTitle` stays as it is. The month line below it
becomes the list's row: `IconButton` (`ic_chevron_left`), the month text centered with
`weight(1f)`, `IconButton` (`ic_chevron_right`, `enabled = state.month < state.currentMonth`).
A disabled `IconButton` is dimmed by Material 3, so no extra styling is needed. The month text keeps
its current style, `bodyMedium` / `onSurfaceVariant`, rather than the list's `titleMedium`, because
the heading above it is already `titleMedium` and two equally heavy lines would compete. The row
duplicates about twenty lines of the list header instead of becoming a shared composable (see
Non-Goals). The row lives inside the `LazyColumn`, like the list's, so it scrolls with the content.
*Alternative:* pin the row above the `LazyColumn`. Rejected: it breaks the "copy the existing
screen" rule and the two tabs would behave differently.

**7. Strings: `HomeStrings` gets its own pair.**
`previousMonth` ("Предыдущий месяц") and `nextMonth` ("Следующий месяц") are added to
`HomeStrings` and filled in `Russian.kt`, used as the arrows' `contentDescription`. They match the
text of the "Операции" pair, but the project rule is one feature, one string group, so they are not
shared.

**8. Nothing is saved.**
The month lives only in `HomeState`. There is no preference, no saved state handle and no
`LaunchedEffect`. A fresh launch builds the initial state from today's date, which is the spec's
"The choice is not remembered". What a tab switch does to the model is left to Voyager, the same
as for the list tab, and the spec does not promise either behavior.

## Risks / Trade-offs

- [The header shows the new month for a moment while the chart still shows the old one] → Accepted;
  the list screen behaves the same and a local Room emission is quick. Clearing the chart is worse
  (decision 5).
- [The arrows scroll out of view with a long category list, so the user scrolls up to switch months]
  → Accepted for consistency with the "Операции" tab (decision 6). Revisit if it annoys on a device.
- [The current month stays fixed after midnight, so on the last day of a month the next month cannot
  be reached until the app restarts] → Inherited from the list screen, not fixed here.
- [Quick repeated taps each restart the observation] → The last tap wins; a cancelled collector
  writes nothing (decision 4), so no stale month can overwrite a newer one.
- [Layout and the dimmed arrow are not seen on a screen] → The app is not run in this change (see
  `tasks.md`); the look is judged when someone first opens the Home tab on a device.
- [The archive step has to rename a requirement and modify it under the new name] → The change was
  checked with `openspec validate "home-month-switcher" --strict`; archiving merges it into the main
  spec and the result is verified there.

## Migration Plan

No database or dependency change: no version bump, no new files in `shared/schemas/`. Rollback is
reverting the commit. Commit only when asked.

## Open Questions

- Whether the left-aligned heading above a centered month row looks balanced on a device; if not,
  center the heading too. This changes no spec and no task.
