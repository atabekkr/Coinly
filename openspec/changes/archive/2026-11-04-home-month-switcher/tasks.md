# Tasks

The project has no automated tests, so each group is verified by building the Android app
(`./gradlew :androidApp:assembleDebug`) and by reading the code against the spec. Running the app on
a simulator, emulator or device is not required; the reply says that it was not run. The files
touched are `HomeEvent.kt`, `HomeState.kt`, `HomeScreenModel.kt`, `HomeScreen.kt`,
`StringResources.kt` and `Russian.kt`. Do not touch the data layer, `di/`, `build.gradle.kts`,
`libs.versions.toml`, Koin startup or root navigation. Problems noticed outside this change are
reported, not fixed. Do not commit unless asked.

## 1. Strings

- [x] 1.1 Add `previousMonth` and `nextMonth` to `HomeStrings` in `StringResources.kt` and fill them in `Russian.kt` with "Предыдущий месяц" and "Следующий месяц"; verify `./gradlew :androidApp:assembleDebug` compiles the two files (a Home composable still uses no `TransactionStrings`)

## 2. Home event, state and ScreenModel

- [x] 2.1 Add `OnPreviousMonthClick` and `OnNextMonthClick` to `HomeEvent` (keep `Idle`) and `currentMonth: YearMonth` (no default) to `HomeState`; in `HomeScreenModel` build the initial state as `currentMonth().let { HomeState(month = it, currentMonth = it) }`, keep `observeJob: Job?`, make `observeExpenses(month)` cancel it and launch the existing collector (updates only `categories` and `total` through `mutableState.update`), add `changeMonth(month)` (`setState(month = month)`, then `observeExpenses(month)`), handle previous with `minusMonth()` and next with `plusMonth()` only when `next <= state.value.currentMonth`, and add `month: YearMonth? = null` to `setState` with `month ?: it.month`; verify the Android build passes and, by reading, that the collector never touches `isLoading` or `month`, `init` goes through `observeExpenses`, the Koin registration is unchanged and `currentMonth` is not a `setState` parameter (design decision 3)

## 3. Home screen

- [x] 3.1 In the header item of `HomeScreen` keep the `expensesTitle` heading and replace the month line with a `Row` copied from the `TransactionListScreen` header: `IconButton` with `ic_chevron_left` sending `OnPreviousMonthClick`, the `formatMonth(state.month)` text centered with `weight(1f)` in `bodyMedium` / `onSurfaceVariant`, `IconButton` with `ic_chevron_right` sending `OnNextMonthClick` and `enabled = state.month < state.currentMonth`, each icon with `strings.home.previousMonth` / `strings.home.nextMonth` as its `contentDescription`; leave the empty message, the ring, the rows and the `isLoading` branch unchanged; verify the Android build passes and, by reading, that the header item is outside the `categories.isEmpty()` branch, so it is shown for a month without expenses too

## 4. Build checks

- [x] 4.1 Run `./gradlew :androidApp:assembleDebug` and `./gradlew :shared:compileKotlinIosSimulatorArm64`; verify both succeed, and with `git status --short` that this change touched only the six files named above (no file under `data/`, `di/`, `screen/app/`, no `build.gradle.kts`, no `libs.versions.toml`, no new file in `shared/schemas/`)

## 5. Check against the specs (the app is not run)

- [x] 5.1 Read the model and the screen against "Navigate between months": walk "Go to an earlier month", "Return toward the present", "Current month is the last one", "Going back with no data" and "The choice is not remembered"; verify each is covered, in particular that the next arrow is disabled and the next event is ignored exactly when `month == currentMonth`, that going back has no lower bound, and that nothing saves the month anywhere
- [x] 5.2 Work the month arithmetic by hand with `currentMonth = 2026-10`: previous gives 2026-09, next gives 2026-10, a second next is ignored because 2026-11 is after the current month, and from 2026-01 previous gives 2025-12; verify each result against `minusMonth` / `plusMonth` and the guard, and that the query is called with the new month's `firstDay` / `lastDay` through `observeMonthExpensesByCategory`
- [x] 5.3 Read the collector and the restart against "Home shows the expense breakdown of one month", "Month without expenses" and "Breakdown reflects changes immediately": the old observation is cancelled before the new one starts, only the displayed month's flow writes `categories` and `total`, the old chart stays until the new emission, an expense dated in another month cannot reach the state, and a month with only income yields no rows and therefore the empty message with the heading and arrows still shown; verify this covers "First view", "Other months are not counted", "No transactions", "Only income", "After adding an expense", "After deleting the only expense", "Moved to another month" and "Adding an income"

## 6. Close out

- [x] 6.1 Run `openspec validate "home-month-switcher" --strict`; verify it reports the change as valid after any spec edits
- [x] 6.2 Go through every scenario of the delta spec for `home-expense-breakdown`; verify each is covered by a task in group 5 or listed with the reason it was not checked, and that the reply states the app was not run, so nothing is confirmed visually
- [x] 6.3 Write the reply with the list of decisions and assumptions made on the way (`currentMonth` left out of `setState` like the list model, the month text kept at `bodyMedium`, the arrow row scrolling with the content, the old chart kept during a switch, the heading alignment left as it was, the duplicated header row instead of a shared composable) and any problem noticed outside the change

## Workflow follow-up

- Archive the change so the renamed requirement and "Navigate between months" are merged into `openspec/specs/home-expense-breakdown/spec.md`.
- Verify `openspec list --specs` shows `home-expense-breakdown` with 11 requirements.
- Optional, not required: someone looks at the Home tab on an Android device or emulator and checks that the heading above a centered month row looks balanced; if not, center the heading too (design, Open Questions).
- Update the "Current state" and Home notes in `CLAUDE.md` when the change ships (only if asked).
