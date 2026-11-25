# Tasks

The project has no automated tests, so each group is verified by building the Android app
(`./gradlew :androidApp:assembleDebug`) and by reading the code against the spec. Running the app on
a simulator, emulator or device is not required; the reply says that it was not run. Do not touch
`build.gradle.kts`, `libs.versions.toml`, Koin startup or root navigation. Problems noticed outside
this change are reported, not fixed. Do not commit unless asked.

## 1. Data layer

- [x] 1.1 Add the per-category expense query to `TransactionDao` (SQL in design decision 1: join with `categories`, `type` / `from` / `to` parameters, `GROUP BY c.id`, `ORDER BY total DESC, c.sortOrder`, returns a `Flow`) and a plain result class next to `TransactionTotal`; verify the Android build (`./gradlew :androidApp:assembleDebug`) passes Room's compile-time query validation
- [x] 1.2 Add the `CategoryExpense` model in `data/transaction/model/` and `TransactionRepository.observeMonthExpensesByCategory(month: YearMonth)`, passing `TransactionType.EXPENSE` and `month.firstDay` / `month.lastDay` and mapping rows inline; verify the shared module compiles and that no entity leaks out of the data layer

## 2. Strings and palette

- [x] 2.1 Add `expensesTitle`, `expensesTotal`, `expensesEmpty` and `lessThanOnePercent` to `HomeStrings` and fill them in `Russian.kt` with "Расходы по категориям", "Всего", "Нет расходов за этот месяц" and "<1%"; verify both files compile and no other feature's strings are used
- [x] 2.2 Add `CategoryColors` (10 colors, values in design decision 6) to `ui/theme/Color.kt`; verify there is no `Color(0x...)` outside `Color.kt` after the whole change (`grep -rn "Color(0x" shared/src`)

## 3. Home state and ScreenModel

- [x] 3.1 Extend `HomeState` with `month: YearMonth`, `categories: List<CategoryExpense>` and `total: Long`; in `HomeScreenModel` build the initial state from the current month (own private `currentMonth()`), collect `observeMonthExpensesByCategory` in `init` with `screenModelScope.launch`, and update only `categories` and `total` through `mutableState.update { it.copy(...) }`, with `total` as the sum of the rows; leave `HomeEvent`, `setState` and the Koin registration unchanged; verify the Android build passes and the collector never touches `isLoading`

## 4. Home screen

- [x] 4.1 Replace the placeholder in `HomeScreen` with `Scaffold` + `TopAppBar` (`strings.home.title`) + `LazyColumn`: header item (`expensesTitle` and `formatMonth(state.month)`), the empty message when `categories` is empty, otherwise the ring item and the rows; keep the `isLoading` spinner branch; verify the Android build passes and, by reading, that the header is always shown and that "Нет расходов за этот месяц" replaces the ring and rows exactly when `categories` is empty
- [x] 4.2 Add a private ring composable in `HomeScreen.kt`: `Canvas` stroked arcs starting at −90° clockwise in the order of `categories`, a 2° gap only with more than one slice, a minimum visible sweep, a full 360° closed ring for one category, colors from `CategoryColors[sortOrder % size]`, and a center `Column` with `expensesTotal` and the total without a sign; verify the Android build passes and, by hand calculation on the spec's four categories (50 / 25 / 15 / 10%), that the sweeps are 180° / 90° / 54° / 36° before the gap and the first arc starts at the top
- [x] 4.3 Add the category rows (`ListItem` copying `TransactionRow`: color dot + emoji, name, amount without a sign followed by `strings.common.currency`, percent with "%" or `lessThanOnePercent` when the share is below 1%), with the percent sign as a private constant in `HomeScreen.kt`; verify the Android build passes and, by reading, that a row's dot and its slice take the color from the same expression

## 5. Check against the specs (the app is not run)

- [x] 5.1 Read the query and the repository method and confirm they count only `EXPENSE` transactions dated inside the current month, return one row per category with `SUM(amount)`, and order ties by `sortOrder`; verify this covers "Income is ignored", "Other months are not counted", "Category without expenses", "Equal amounts" and "Only income"
- [x] 5.2 Work the spec's examples by hand against the code: 620 000 / 310 000 / 186 000 / 124 000 gives total "1 240 000 сум" through `formatBalance`, rows "620 000 сум" and "50%", "25%", "15%", "10%"; 600 000 / 394 000 / 6 000 gives "60%", "39%", "<1%"; verify each result matches the "Whole percents", "Small share", "Month total" and "Row content" scenarios
- [x] 5.3 Read the ring drawing and confirm: first arc at −90° and clockwise, 2° gap only with more than one slice, a minimum sweep so a tiny share stays visible, a 360° ring with no gap for one category; verify this covers "Shares of the ring", "Biggest first" and "Single category"
- [x] 5.4 Read `CategoryColors` and its use: ten different values, index 8 gray, slices and rows use the same `CategoryColors[sortOrder % size]` expression, nothing but `Color.kt` contains `Color(0x...)` (`grep -rn "Color(0x" shared/src`); verify this covers "Slice and row match", "Color does not change with the data" and "Default categories are told apart", and that colors repeating after ten categories is confirmed by reading only, since more categories cannot be created yet
- [x] 5.5 Read the ScreenModel and screen and confirm: the repository flow is the only writer of `categories` and `total`, `isLoading` is never touched, the content is a `LazyColumn` so a long list scrolls, and a month with only income yields no rows and therefore the empty message; verify this covers "Breakdown reflects changes immediately", "Many categories" and "Month without expenses"

## 6. Close out

- [x] 6.1 Run `openspec validate "home-expense-chart" --strict`; verify it reports the change as valid
- [x] 6.2 Go through every scenario of `home-expense-breakdown`; verify each is covered by a task in group 5 or listed with the reason it was not checked, and that the reply states the app was not run, so nothing is confirmed visually
- [x] 6.3 Write the reply with the list of decisions and assumptions made on the way (the `TopAppBar`, the private duplicate of `currentMonth()`, amounts without a sign as an exception to the CLAUDE.md sign rule, rounding rule, palette values and that the pale colors were not seen on a screen) and any problem noticed outside the change

## Workflow follow-up

- Archive the change so `home-expense-breakdown` moves into `openspec/specs/`.
- Verify `openspec list --specs` shows `home-expense-breakdown`.
- Optional, not required: someone looks at the Home tab on an Android device or emulator (emoji do not render on the iOS 26.3 simulator) and adjusts the pale palette values (yellow, pink, gray) in `Color.kt` if they look weak.
- Update the "Current state" and Home notes in `CLAUDE.md` when the change ships (only if asked).
