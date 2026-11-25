# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

## Project Overview

**Coinly** is a personal finance app: income and expenses, categories, monthly spending limits and
savings. Built with Kotlin Multiplatform + Compose Multiplatform for Android and iOS.

- Offline-first: all data lives in a local Room database. No backend, no networking, no auth.
- Built incrementally: MVP first, one feature at a time, each one requested explicitly.
- Current state:
  - Transactions: list by month (`screen/transaction/list/`) and add / edit screen
    (`screen/transaction/edit/`).
  - Home (`screen/home/`): balance, the month's income / expenses and recent transactions.
  - Report (`screen/report/`): a month's expenses or income by category as a ring and a list.
  - Limits (`screen/budget/list/`, `screen/budget/edit/`): monthly limit per expense category.
  - Not built: Savings (the "Goals" block of the Budgets design), custom categories, Settings,
    Profile, Notifications. The last three are "in development" placeholder screens.
- The look follows the design handoff in `docs/design/` (README with tokens, screenshots, HTML
  sources). `CLAUDE.md` wins where they disagree.
- UI language is Russian. Localization is postponed.

## Rules (mandatory)

These rules override your own preferences. When a rule conflicts with "better" code, follow the
rule and mention the alternative in your reply.

### Scope

- Do only what the task asks. Do not refactor, rename, reformat or "improve" adjacent code.
- One feature at a time. The MVP roadmap below is context, not a to-do list: do not create screens,
  entities or helpers for features that were not requested.
- Never delete files, functions or commented-out code unless the task says so.
- No new dependencies, modules, base classes, generic helpers/extensions or abstractions without
  explicit approval.
- Problems noticed outside the task: mention them in the reply, do not fix them.
- Ambiguous requirement or missing info (business rule, which field is required, edge case): ask
  before implementing. Never invent values or rules.
- Task touches more than one layer (data + screen) or more than ~3 files: propose a plan first and
  wait for approval.
- Commit only when asked.
- At the end of a task, list the decisions and assumptions you made on your own.

### Copy existing patterns

- Before writing a new screen or repository, find the closest existing one and copy its structure.
  Do not mix patterns from different screens.
- Screen = 4 files: `XxxScreen.kt`, `XxxScreenModel.kt`, `XxxState.kt`, `XxxEvent.kt`, placed in
  `screen/<feature>/` for a single-screen feature (e.g. `screen/home/`) or in
  `screen/<feature>/<screen_name>/` when the feature has several screens
  (e.g. `screen/transaction/edit/`).
- Exception: a placeholder for a feature that is not built yet (`screen/settings/`,
  `screen/profile/`, `screen/notification/`) is a single `XxxScreen.kt` whose body is the
  `InDevelopment` component. It has no state, so no ScreenModel / State / Event. When the feature
  is built, it becomes a normal 4-file screen.
- An amount form (new transaction, new limit) follows `TransactionEditScreen` /
  `BudgetEditScreen`: round close button and centred title, the amount shown large, `CategoryGrid`,
  `AmountKeypad`, `PrimaryButton`.
- Screen: Voyager `class XxxScreen : Screen` with `@Composable override fun Content()`; gets the model
  via `koinScreenModel()` and reads `model.state.collectAsState()`.
- Event: `sealed interface XxxEvent`. Reset event is `data object Idle`.
- ScreenModel: `StateScreenModel<XxxState>(XxxState()), KoinComponent`, repositories via
  `by inject<...>()`, one public `fun onEvent(event)` with `when`, state changes through the
  private `setState(...)`.
- `setState` convention: one-shot flags (`loading`, `errorMessage`, `saveSuccess`, ...) default to
  reset; persistent data defaults to `?: it.field`. A new State field must also be added to
  `setState`, except a value set once when the ScreenModel is created and never changed (e.g.
  `currentMonth` in `TransactionListState` and `HomeState`) and the data fields written by a Room
  `Flow` collector (see "Observing a Room `Flow`" below).
- Register ScreenModels as `factory` in `screenModelModule()` and repositories as `single` in
  `di/Koin.common.kt`.
- Errors: `errorMessage` in State → Material 3 `AlertDialog` on the screen → `Idle` resets it.
  Repository writes in the ScreenModel are wrapped in `try / catch`: rethrow
  `CancellationException`, map any other exception to a feature string in `errorMessage`
  (reference: `TransactionEditScreenModel.save`).
- Screen arguments (e.g. the id of an item to edit) go to the ScreenModel constructor through Koin
  parameters: `koinScreenModel<XxxScreenModel> { parametersOf(id) }` on the screen,
  `factory { params -> XxxScreenModel(params.getOrNull()) }` in the module. The ScreenModel loads its
  data in `init`. Do not load data from a `LaunchedEffect` event: it runs again every time the
  screen returns to the composition and can overwrite user input.
- A screen with arguments overrides `key = uniqueScreenKey`; otherwise Voyager can give a new
  instance the cached ScreenModel of a previous one.
- Dialogs (date picker, delete confirmation) are one-shot flags in State (`showDatePicker`,
  `showDeleteDialog`), opened by an event and closed by `Idle`.
- Observing a Room `Flow`: the collector updates only the observed data fields with
  `mutableState.update { it.copy(...) }` and never touches one-shot flags. This is the only allowed
  exception to "state changes through `setState`" (going through `setState` would reset e.g.
  `saveSuccess` when an emission arrives right after a save).

### Data layer (local only)

- Room 3: artifacts, packages and the Gradle plugin are `androidx.room3`, not `androidx.room`. Type
  converters are `@ColumnTypeConverter(s)`. `@Relation` takes arrays:
  `@Relation(parentColumns = [...], entityColumns = [...])`. DAO functions must be `suspend` or
  return `Flow`.
- Database: `CoinlyDatabase` in `core/database/`, `BundledSQLiteDriver`, constructed through
  `@ConstructedBy` + `expect object CoinlyDatabaseConstructor`. Database builders are plain
  functions in `androidMain` / `iosMain` (different signatures, not expect/actual), provided by
  `platformModule()` (`di/Koin.android.kt`, `di/Koin.ios.kt`).
- Default data is seeded in `PrepopulateCallback.onOpen` when the table is empty, not in `onCreate`:
  Room does not call `onCreate` after a destructive migration.
- Foreign keys are enforced (SQLite error 787 on violation).
- Repository: `class XxxRepository : KoinComponent` with the DAO via `by inject`. Data a screen shows
  continuously is read as a `Flow` of models; one-shot reads (e.g. loading an item to edit) and
  writes are `suspend fun`. No `UiState` wrapper for local data.
- Entities stay in the data layer. Mapping via `XxxEntity.toModel()` / `Xxx.toEntity()`. ScreenModel
  and UI use models only, never entities.
- Money: `Long` in tiyin everywhere (DB, models, State). Convert and format only through
  `core/common/util/MoneyConverters` (`convertToTiyin`, `convertToSums`, `formatBalance`). Never
  hand-written `* 100` / `/ 100`.
- Every entity has a UUID id and `createdAt` / `updatedAt` (keeps future sync possible).
- Balances and totals are computed with SQL aggregates, never stored.
- Dates: kotlinx-datetime. `Instant` and `Clock` come from `kotlin.time`, not `kotlinx.datetime`.
- Schema changes: bump the database version and add a migration — ask first. Prefer
  `AutoMigration(from, to)` in `@Database(autoMigrations = ...)` when Room can generate it.
  There is no destructive fallback: real data already lives on a device, so a missing migration
  must fail instead of wiping it. Room writes schemas to `shared/schemas/`; commit them (auto
  migrations are generated from them).

### UI

- Theme: `CoinlyTheme` (`ui/theme/Theme.kt`) wraps Material 3 with the design's color scheme,
  typography (`Type.kt`) and shapes; `AppContent` applies it. Light theme only — no dark theme was
  designed.
- Colors: screens take them from `MaterialTheme.colorScheme` or the named colors in
  `ui/theme/Color.kt` (`IncomeColor`, `ExpenseAccentColor`, `WarningBarColor`, `Hero*Color`, ...).
  No `Color(0x...)` in screens; a new color is a new named value in `Color.kt`.
- Color scheme roles: `primary` violet (hero, "+" button, primary button, active tab),
  `background` screen ground, `surface` white cards, `onSurfaceVariant` secondary text,
  `outlineVariant` dividers and neutral bar tracks, `surfaceVariant` control tracks,
  `primaryContainer` violet tint, `error` / `errorContainer` over-limit.
- Font: Inter only (`composeResources/font/`, weights 400 / 500 / 600 / 700). The design's Space
  Grotesk is not used: it has no Cyrillic.
- Text styles come from `MaterialTheme.typography`; each slot's use is commented in `Type.kt`
  (e.g. `headlineSmall` screen title, `titleLarge` section heading, `titleSmall` row title,
  `titleMedium` row amount, `bodySmall` secondary line, `labelSmall` tab label). Do not hand-write
  font sizes in screens.
- Shapes: `MaterialTheme.shapes` — `large` (20) list and summary cards, `extraLarge` (24) chart
  and hero cards, `medium` (16) tiles, `small` (12) emoji tiles and segments.
- Layout: screen side padding 20, card inner padding 16, touch targets at least 44.
- Shared components in `ui/components/` — use them instead of rebuilding the same look:
  `ScreenHeader` (tab title + trailing control), `MonthSwitcher` (pill with ‹ ›), `ListCard` +
  `ListCardDivider`, `TransactionRow`, `SummaryCard`, `EmojiTile`, `SegmentedControl`,
  `CategoryGrid`, `AmountKeypad`, `PrimaryButton`, `RoundIconButton`, `InDevelopment`.
- Amounts: income green (`IncomeColor`) with "+", expense `onSurface` with "−"; zero has no sign.
  Exceptions, where a label already says what the amount is, so it has no sign and no color: the
  Report breakdown (under the "Расходы" / "Доходы" switch), the income / expense cards on Home,
  and the Budgets screen.
- The currency word follows the number (`strings.common.currency`); next to a large amount (hero
  balance, entered amount) it is a smaller, secondary-colored text.
- Category colors: `categoryColor(sortOrder)` (= `CategoryColors[sortOrder % size]`) in charts
  and bars, `categoryTint(sortOrder)` for the emoji tile background, both in `ui/theme/Color.kt`.
  A category's color never depends on its share or rank.
- Amount input: the on-screen `AmountKeypad` (digits and backspace, no decimal key) sends
  `ChangeAmount(state.amount + digit)`; State keeps the raw digits and the screen formats them
  with `formatBalance(convertToTiyin(...))`. Never store formatted text in State. The system
  keyboard is used only for free text (the note). `AmountVisualTransformation` is the pattern for
  a masked text field and is currently unused.
- Limit states on Budgets: normal below 80% of the limit (`primary` on `primaryContainer`),
  warning from 80% (`WarningBarColor` on `ExpenseTintColor`, text `ExpenseAccentColor`), danger
  from 100% (`error` on `errorContainer`).
- Icons: vector drawables in `composeResources/drawable/`; no icon library. Either Material
  Symbols (960 viewport) or classic Material Icons (24 viewport). Strip
  `android:tint="?attr/..."` and use `#FF000000` fills when adding one.
- Category icons are emoji. The iOS 26.3 simulator draws every emoji as a "?" box, native SwiftUI
  text included — a simulator bug, not ours; check emoji on Android or a real iPhone.
- Material `DatePicker` shows month / weekday names in the device language (Compose limitation).

### Strings

- No hardcoded user-visible text in Kotlin, including labels, placeholders and error messages. The
  only exception is the app name "Coinly".
- Strings live in `ui/localization/`: `StringResources` interface grouped by feature, implemented in
  `Russian.kt` (each group is a class, e.g. `CategoryStrings`). Composables use
  `LocalStrings.current`, ScreenModels use `AppRes.strings`. A new language later = one more
  implementation file.
- Use the feature's own string group. Do not reuse another feature's strings. `common` holds what
  shared components show (OK, cancel, back, close, currency, month names, "В разработке").

### Validation

- Validation and building the model to save live in the ScreenModel. The submit button sends one
  event with no data (e.g. `OnSaveClick`); the ScreenModel reads `state.value`, validates and builds
  the model.
- Checks run in the form's field order. The first failing check sets `errorMessage`, which is shown
  by the screen's `AlertDialog`; `Idle` dismisses it. No per-field error flags in State.
- Input filtering (digits only, max length) stays in the `Change*` event handler.

### Do not touch without asking

- `build.gradle.kts` files, `gradle/libs.versions.toml`, `gradle.properties`.
- Root navigation in `screen/app/AppContent.kt`.
- Koin startup: `androidApp/.../CoinlyApplication.kt`, `iosApp/iosApp/iOSApp.swift`.
- Database migrations.

## Dependency Version Rules

- Always check `gradle/libs.versions.toml` before adding or changing a dependency. Do not guess
  versions, do not upgrade unless asked, prefer stable releases.
- KSP 2.3.0+ is no longer tied to the Kotlin version. The Room compiler is added per target:
  `kspAndroid`, `kspIosArm64`, `kspIosSimulatorArm64` (the catch-all `ksp(...)` fails in KMP).
- `androidx.sqlite:sqlite-bundled` must match the Room line (Room 3.0.x ↔ sqlite 2.7.x).
- `shared` compiles with `-Xexpect-actual-classes`: it silences the "expect/actual classes are in
  Beta" warning from Room's `expect object` constructor.
- Voyager is no longer actively developed. Its version name is the Kotlin and Compose Multiplatform
  versions it was built with (`2.2.21-1.10.3`), older than ours (Kotlin 2.4.20, CMP 1.12.1); this
  combination was verified to run on Android and iOS on 2026-10-11. After upgrading Kotlin, Compose
  Multiplatform or Voyager, run the app on the iOS simulator, not only compile it: Kotlin/Native
  linkage problems can surface only at runtime.
- Koin: stay on 4.2.2 or newer (4.2.0 / 4.2.1 had resolver bugs).

## Architecture Overview

**Stack:** Kotlin Multiplatform · Compose Multiplatform · Material 3 · Voyager (navigation + screen
models) · Koin (DI) · Room 3 + bundled SQLite · kotlinx-datetime

### Modules

- `shared/` — all app code (UI, ScreenModels, data). Targets: `android`, `iosArm64`,
  `iosSimulatorArm64`.
- `androidApp/` — `CoinlyApplication` (calls `initKoin { androidContext(...) }`) and `MainActivity`.
- `iosApp/` — SwiftUI wrapper: `iOSApp.swift` calls `Koin_commonKt.doInitKoin(config: nil)`,
  `ContentView` hosts `MainViewController()` from `shared/src/iosMain`.

### Package structure (`shared/src/commonMain/kotlin/com/atabekdev/coinly/`)

```text
di/                  Koin.common.kt: initKoin(), Koin modules (platformModule() is expect/actual)
core/common/util/    MoneyConverters, DateFormatters (formatDate, formatDayMonth, formatMonth)
core/database/       CoinlyDatabase, DateConverters, PrepopulateCallback (builders in androidMain / iosMain)
data/<feature>/      XxxRepository + model/ + local/ (XxxEntity, XxxDao, Room relation classes)
screen/app/          AppContent (CoinlyTheme + root Navigator), MainScreen (bottom tabs)
screen/<feature>/    XxxScreen + XxxScreenModel + XxxState + XxxEvent
ui/components/       shared composables, visualTransformations/ (AmountVisualTransformation)
ui/localization/     StringResources, Russian, AppRes (LocalStrings + AppRes)
ui/theme/            Theme.kt (CoinlyTheme), Type.kt (Inter typography), Color.kt (tokens, CategoryColors)
```

Features: `data/category/`, `data/transaction/`, `data/budget/`; `screen/home/`, `screen/report/`,
`screen/budget/list/`, `screen/budget/edit/`, `screen/transaction/list/`,
`screen/transaction/edit/`, and the placeholders `screen/settings/`, `screen/profile/`,
`screen/notification/`.

### Navigation

- `AppContent` → root `Navigator(MainScreen())`. `MainScreen` hosts a `TabNavigator` and draws the
  bottom bar itself (not the Material `NavigationBar`): a white bar with a notch and the round "+"
  button in the centre. It is a pure container, so it has no ScreenModel / State / Event.
- Tabs, in order: `HomeScreen`, `ReportScreen`, "+", `BudgetListScreen`, `SettingsScreen`. The "+"
  button is not a tab: it pushes `TransactionEditScreen()` on the root navigator.
- A tab is an `object XxxScreen : Tab` (otherwise the usual 4-file screen) with `options`
  (index, title from strings, icon). The bar has four tab slots around the "+"; adding a fifth
  tab is a design decision — ask first.
- Tabs live in a nested navigator. Detail screens are pushed on the root one so they cover the
  tab bar: `checkNotNull(LocalNavigator.currentOrThrow.parent).push(...)`. Pushed from Home:
  `TransactionListScreen` ("Все"), `TransactionEditScreen`, `ProfileScreen`, `NotificationScreen`;
  from Budgets: `BudgetEditScreen`.
- Insets: `MainScreen` pads the tab content by the bar height and consumes the navigation bar
  inset, so a tab does not add the bottom inset again. The top inset is left to the screens,
  because the Home hero runs under the status bar: a tab or pushed screen with a `ScreenHeader`
  (or its own header row) adds `Modifier.statusBarsPadding()` to it.
- A screen pushed on the root navigator has a `RoundIconButton` in its header: back arrow for a
  list or placeholder, close "X" for an amount form.

### Screen pattern

```kotlin
// State — immutable data class
data class XxxState(
    val name: String = "",
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
)

// Events — sealed interface
sealed interface XxxEvent {
    data class ChangeName(val value: String) : XxxEvent
    data object OnSaveClick : XxxEvent
    data object Idle : XxxEvent
}

// ScreenModel
class XxxScreenModel : StateScreenModel<XxxState>(XxxState()), KoinComponent {
    private val repository by inject<XxxRepository>()

    fun onEvent(event: XxxEvent) {
        when (event) {
            is XxxEvent.ChangeName -> setState(name = event.value)
            XxxEvent.OnSaveClick -> save()
            XxxEvent.Idle -> setState()
        }
    }

    private fun save() {
        // validate state.value in field order; first failure → setState(errorMessage = ...) and return
        screenModelScope.launch {
            repository.save(...)
            setState(saveSuccess = true)
        }
    }

    private fun setState(
        name: String? = null,
        loading: Boolean = false,
        saveSuccess: Boolean = false,
        errorMessage: String? = null,
    ) {
        mutableState.update {
            it.copy(
                name = name ?: it.name,
                isLoading = loading,
                saveSuccess = saveSuccess,
                errorMessage = errorMessage,
            )
        }
    }
}

// Screen
class XxxScreen : Screen {
    @Composable
    override fun Content() {
        val model = koinScreenModel<XxxScreenModel>()
        val state by model.state.collectAsState()
        // ...
    }
}
```

## Planning: OpenSpec

Larger changes are planned and tracked with OpenSpec (CLI `openspec`, config in
`openspec/config.yaml`, schema `spec-driven`). For a task that needs a plan (more than one layer or
~3 files), the plan can be an OpenSpec change.

- `openspec/specs/<capability>/spec.md` — main specs: the current behavior of a capability (e.g.
  `home-expense-breakdown`). They are updated when a change is archived or synced. Note:
  `home-expense-breakdown` describes the breakdown that now lives on the Report tab; the redesign
  and the Limits feature were built without OpenSpec changes, so the specs do not cover them yet.
- `openspec/changes/<name>/` — one change in progress: `proposal.md`, a delta spec in
  `specs/<capability>/spec.md` (ADDED / MODIFIED / REMOVED / RENAMED requirements), `design.md` and
  `tasks.md`. A finished change is moved to `openspec/changes/archive/YYYY-MM-DD-<name>/`.
- Commands (`.claude/commands/opsx/`): `/opsx:explore` (think it through), `/opsx:propose` (create
  the change and its artifacts; planning only, no code), `/opsx:apply` (implement `tasks.md` and tick
  the checkboxes), `/opsx:archive` (merge the delta specs into `openspec/specs/` and archive),
  `/opsx:sync`, `/opsx:update`. Check a change with `openspec validate "<name>" --strict`.
- The project has no automated tests, so tasks are verified by building
  (`./gradlew :androidApp:assembleDebug`, `./gradlew :shared:compileKotlinIosSimulatorArm64`) and by
  reading the code against the spec.

## Product: MVP Scope and Decisions

### MVP features (build one at a time, only when asked)

1. **Transactions** (done) — income / expense: amount, category, date, note. List by month, grouped
   by day. Edit and delete.
2. **Categories** (default set only) — default set + custom ones, separate for income and expense;
   icon is an emoji.
3. **Limits** (done, "Бюджеты" tab) — monthly limit per expense category with progress: spent /
   left, warning color at 80%, red at 100%+.
4. **Savings** (not built) — savings accounts ("копилки") with an optional goal amount; deposit and
   withdraw. In the design it is the "Goals" block of the Budgets screen.
5. **Home** (done, without limits) — balance, income and expenses of the month, recent
   transactions.
6. **Statistics** (done, "Отчёт" tab) — expenses or income of a month by category: sum and
   percentage.

### Agreed decisions

- Single currency: UZS (store the currency code anyway).
- One main account + savings accounts. Several regular accounts (cash, cards) is a later feature.
- Moving money to or from savings is a `TRANSFER`, never an expense or income.
- A limit is set once per category and applies to every month until changed. Month = calendar month.
- Navigation: bottom tabs "Главная", "Отчёт", "Бюджеты", "Настройки" with a centre "+" button
  (new transaction). The transaction list is not a tab: it opens from "Все" on Home. Detail
  screens (e.g. transaction edit) open over the tabs.
- Month switcher (Home, Report, transaction list): opens on the current month, back without
  limit, no months after the current one (no future dates); the chosen month is not remembered.
- Transaction list: one calendar month, newest first, grouped by day ("Сегодня", "Вчера",
  "11 октября"), month income / expense totals on top.
- Home:
  - Balance = all income minus all expenses over all time (SQL aggregate). There is no initial
    balance and no account yet; the balance does not depend on the chosen month.
  - Under the balance: the chosen month's income minus expenses ("+… сум за месяц").
  - Income / expense cards of the chosen month.
  - The 10 newest transactions of the chosen month, grouped by day ("Сегодня, 15 ноября",
    "Вчера, 14 ноября", "11 ноября"); the second line of a row is the note (transactions have no
    time).
  - The avatar shows "C" (app name initial); it and the bell open placeholder screens.
- Report: expenses or income (switch) of one calendar month by category — ring + list, biggest
  first; the row bar is scaled to the biggest category, the percent is the share of the total
  ("<1%" below 1%). "В среднем за день" = total / days passed for the current month, / days in
  the month for a past one, rounded down to whole sums. "Топ-категория" = the biggest category.
- Budgets (limits):
  - One limit per expense category; the category of an existing limit cannot be changed (delete
    and create again). A new limit offers only expense categories without one.
  - Limit amount in whole sums, > 0, at most 12 digits.
  - The screen shows the current month only, no month switcher.
  - Month budget = sum of all limits; spent = the month's expenses in the categories that have a
    limit; left may be negative ("−"). Percent spent is rounded down and may exceed 100.
  - "Осталось дней" = days of the month after today.
- Placeholder screens (Settings, Profile, Notifications) show "В разработке"; notifications stay
  out of MVP.
- Out of MVP: multi-currency, backend / sync, recurring payments, notifications, export, SMS / bank
  import, subcategories, PIN / biometrics.

Transactions:
- No accounts yet: a transaction has no `accountId` and its type is `INCOME` or `EXPENSE`.
  `accountId` and `TRANSFER` come with Savings.
- Categories use the same `TransactionType` (`INCOME` / `EXPENSE`); a category never has `TRANSFER`.
- A transaction stores a date without time, today by default; future dates cannot be picked.
  Within a day, sort by `createdAt`.
- Amount input in whole sums from the on-screen keypad, stored in tiyin; amount > 0, at most
  12 digits.
- The add / edit form shows every category of the chosen type as a tile (no "More" tile); the
  save button reads "Сохранить расход" / "Сохранить доход".
- Note is optional, at most 200 characters.
- Default categories (names from `CategoryStrings`) — expense: 🛒 Продукты, 🍽 Кафе и рестораны,
  🚕 Транспорт, 🎬 Развлечения, 🛍 Покупки, 💊 Здоровье, 📱 Связь и интернет, 🏠 Коммунальные услуги,
  📦 Другое; income: 💼 Зарплата, 🎁 Подарки, 💰 Другое.

### Data model

```text
# implemented
categories    id, name, emoji, type (INCOME | EXPENSE), sortOrder, createdAt, updatedAt
transactions  id, type (INCOME | EXPENSE), amount, categoryId (FK, RESTRICT), date, note, createdAt, updatedAt
budgets       id, categoryId (FK, RESTRICT, unique), amount, createdAt, updatedAt

# draft — adjust when implementing
accounts      id, name, type (MAIN | SAVINGS), initialBalance, goalAmount?, currency, createdAt, updatedAt
categories    + isArchived (with the Categories feature)
transactions  + accountId, toAccountId?, type TRANSFER, categoryId? (with Savings)
```

Database version: 2 (`shared/schemas/2.json`). Migrations: 1 → 2 `AutoMigration` (adds `budgets`).

### Open business rules (proposed, not confirmed — ask when implementing the feature)

- Initial balance: `initialBalance` field on the main account, edited by tapping the balance; no
  onboarding. (Today the Home balance is income minus expenses with no initial balance.)
- Deleting a category that has transactions: archive it (hidden from pickers, kept in history).
  What happens to its limit is undecided (today the foreign key is RESTRICT).
- Whether limits appear on Home, as MVP feature 5 lists, is undecided: the design's Home has none.
- Deleting a savings account: only when its balance is zero.
- The main account may go negative (shown in red); withdrawing more than a savings account holds is
  not allowed.
