# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

## Project Overview

**Coinly** is a personal finance app: income and expenses, categories, monthly spending limits and
savings. Built with Kotlin Multiplatform + Compose Multiplatform for Android and iOS.

- Offline-first: all data lives in a local Room database. No backend, no networking, no auth.
- Built incrementally: MVP first, one feature at a time, each one requested explicitly.
- Current state: Transactions feature done — bottom tabs, transaction list by month
  (`screen/transaction/list/`) and add / edit screen (`screen/transaction/edit/`). The "Главная"
  tab (`HomeScreen`) is still empty.
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
- Screen: Voyager `class XxxScreen : Screen` with `@Composable override fun Content()`; gets the model
  via `koinScreenModel()` and reads `model.state.collectAsState()`.
- Event: `sealed interface XxxEvent`. Reset event is `data object Idle`.
- ScreenModel: `StateScreenModel<XxxState>(XxxState()), KoinComponent`, repositories via
  `by inject<...>()`, one public `fun onEvent(event)` with `when`, state changes through the
  private `setState(...)`.
- `setState` convention: one-shot flags (`loading`, `errorMessage`, `saveSuccess`, ...) default to
  reset; persistent data defaults to `?: it.field`. A new State field must also be added to `setState`.
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
- Schema changes before the first release: bump the database version; the destructive migration
  wipes the data. After the first release: real migrations — ask first. Room writes schemas to
  `shared/schemas/`; commit them.

### UI

- No custom design system yet: standard Material 3 components. Colors and text styles from
  `MaterialTheme.colorScheme` / `MaterialTheme.typography` or `ui/theme/Color.kt`; no `Color(0x...)`
  in screens.
- Amounts: income green (`IncomeColor`) with "+", expense `onSurface` with "−"; zero has no sign.
- Input masks (e.g. amount with thousand separators): raw value in State + `VisualTransformation`
  in `ui/components/`. Never store formatted text in State.
- Icons: Material Symbols as vector drawables in `composeResources/drawable/`; no icon library.
  Strip `android:tint="?attr/..."` and use `#FF000000` fills when adding one.
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
- Use the feature's own string group. Do not reuse another feature's strings.

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
- Database migrations (after the first release).

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
screen/app/          AppContent (MaterialTheme + root Navigator), MainScreen (bottom tabs)
screen/<feature>/    XxxScreen + XxxScreenModel + XxxState + XxxEvent
ui/components/       shared composables, visualTransformations/ (AmountVisualTransformation)
ui/localization/     StringResources, Russian, AppRes (LocalStrings + AppRes)
ui/theme/            Color.kt (IncomeColor)
```

### Navigation

- `AppContent` → root `Navigator(MainScreen())`. `MainScreen` hosts a `TabNavigator` with the
  bottom `NavigationBar`; it is a pure container, so it has no ScreenModel / State / Event.
- A tab is an `object XxxScreen : Tab` (otherwise the usual 4-file screen) with `options`
  (index, title from strings, icon). Add a new tab to the `NavigationBar` in `MainScreen`.
- Tabs live in a nested navigator. Detail screens are pushed on the root one so they cover the
  tab bar: `checkNotNull(LocalNavigator.currentOrThrow.parent).push(...)`.
- `MainScreen` pads the tab content with `consumeWindowInsets`, so a tab's own `Scaffold` /
  `TopAppBar` does not add system bar insets a second time.

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

## Product: MVP Scope and Decisions

### MVP features (build one at a time, only when asked)

1. **Transactions** — income / expense: amount, category, date, note. List by month, grouped by day.
   Edit and delete.
2. **Categories** — default set + custom ones, separate for income and expense; icon is an emoji.
3. **Limits** — monthly limit per expense category with progress: spent / left, warning color at
   80%, red at 100%+.
4. **Savings** — savings accounts ("копилки") with an optional goal amount; deposit and withdraw.
5. **Home** — balance, income and expenses of the current month, limits, recent transactions.
6. **Statistics** — expenses of a month by category: sum and percentage.

### Agreed decisions

- Single currency: UZS (store the currency code anyway).
- One main account + savings accounts. Several regular accounts (cash, cards) is a later feature.
- Moving money to or from savings is a `TRANSFER`, never an expense or income.
- A limit is set once per category and applies to every month until changed. Month = calendar month.
- Navigation: bottom tabs "Главная" and "Операции"; "Лимиты" and "Копилки" are added with their
  features. Detail screens (e.g. transaction edit) open over the tabs.
- Transaction list: one calendar month, newest first, grouped by day ("Сегодня", "Вчера",
  "11 октября"), month income / expense totals on top. Months after the current one cannot be
  opened (no future dates).
- Out of MVP: multi-currency, backend / sync, recurring payments, notifications, export, SMS / bank
  import, subcategories, PIN / biometrics.

Transactions:
- No accounts yet: a transaction has no `accountId` and its type is `INCOME` or `EXPENSE`.
  `accountId` and `TRANSFER` come with Savings.
- Categories use the same `TransactionType` (`INCOME` / `EXPENSE`); a category never has `TRANSFER`.
- A transaction stores a date without time, today by default; future dates cannot be picked.
  Within a day, sort by `createdAt`.
- Amount input in whole sums, stored in tiyin; amount > 0, at most 12 digits.
- Note is optional, at most 200 characters.
- Default categories (names from `CategoryStrings`) — expense: 🛒 Продукты, 🍽 Кафе и рестораны,
  🚕 Транспорт, 🎬 Развлечения, 🛍 Покупки, 💊 Здоровье, 📱 Связь и интернет, 🏠 Коммунальные услуги,
  📦 Другое; income: 💼 Зарплата, 🎁 Подарки, 💰 Другое.

### Data model

```text
# implemented
categories    id, name, emoji, type (INCOME | EXPENSE), sortOrder, createdAt, updatedAt
transactions  id, type (INCOME | EXPENSE), amount, categoryId (FK, RESTRICT), date, note, createdAt, updatedAt

# draft — adjust when implementing
accounts      id, name, type (MAIN | SAVINGS), initialBalance, goalAmount?, currency, createdAt, updatedAt
categories    + isArchived (with the Categories feature)
transactions  + accountId, toAccountId?, type TRANSFER, categoryId? (with Savings)
budgets       id, categoryId (unique), amount, createdAt, updatedAt
```

### Open business rules (proposed, not confirmed — ask when implementing the feature)

- Initial balance: `initialBalance` field on the main account, edited by tapping the balance; no
  onboarding.
- Deleting a category that has transactions: archive it (hidden from pickers, kept in history).
- Deleting a savings account: only when its balance is zero.
- The main account may go negative (shown in red); withdrawing more than a savings account holds is
  not allowed.
