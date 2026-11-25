# Design

## Context

- `HomeScreen` is a `Tab` whose content is a centered "Coinly" text, or a spinner while
  `HomeState.isLoading` is true. `HomeScreenModel` has only `HomeEvent.Idle` and `setState(loading)`.
  `HomeScreenModel` is already registered as a `factory` in `screenModelModule()`;
  `TransactionRepository` is already a `single`. Neither needs a Koin change.
- The closest existing screen is `TransactionListScreen` / `TransactionListScreenModel`: a
  `Scaffold` with `TopAppBar`, a `LazyColumn`, `ListItem` rows, and a Room `Flow` collected in
  `init` that updates only data fields with `mutableState.update { it.copy(...) }`. This design
  copies that structure.
- Month math already exists: `YearMonth.firstDay / lastDay` feed `TransactionDao.observeBetween` and
  `observeTotalsBetween`. Dates are stored as ISO strings, so `date BETWEEN :from AND :to` works.
- `categories` has `sortOrder`. The seeding callback writes the index in `defaultCategories()`, so
  the nine default expense categories are 0..8 (the last, "Другое", is 8) and the three income ones
  are 9..11. The `Category` model does not carry `sortOrder`.
- `AppContent` wraps everything in a plain `MaterialTheme { }`, so the app is effectively light-only
  today. There is no existing palette besides `IncomeColor`.
- Money is `Long` tiyin, formatted only through `formatBalance`. Strings come from `HomeStrings` /
  `Russian.kt`; screens read `LocalStrings.current`.

## Goals / Non-Goals

**Goals:**
- Reuse the query for a later Statistics screen without changing it (it takes a `YearMonth`).
- No schema change, no new dependency, no change to files in the "do not touch" list.

**Non-Goals:**
- Month navigation, income breakdown, slice taps, animation, accessibility work beyond what the
  text rows already give.
- Fixing the stale "current month" after midnight; Home inherits it from the list screen.
- Extracting helpers shared with the transaction list (see decision 8).

## Decisions

**1. One SQL aggregate, joined with categories.**
A new `TransactionDao` query returns one row per category:

```sql
SELECT c.id AS categoryId, c.name AS name, c.emoji AS emoji, c.sortOrder AS sortOrder,
       SUM(t.amount) AS total
FROM transactions t INNER JOIN categories c ON c.id = t.categoryId
WHERE t.type = :type AND t.date BETWEEN :from AND :to
GROUP BY c.id
ORDER BY total DESC, c.sortOrder
```

It returns a `Flow` and is read through a new plain result class next to `TransactionTotal`. The
type is a parameter (the repository passes `EXPENSE`) so the SQL has no enum string literal,
matching how `update` already binds `type`. `ORDER BY ... c.sortOrder` makes ties deterministic
(spec: "Equal amounts"). Totals stay computed in SQL, never stored.
*Alternative:* load the month's transactions (`observeBetween`) and group in Kotlin. Rejected: it
moves a sum over every row into the UI layer and goes against "totals are computed with SQL
aggregates".

**2. A flat model, `CategoryExpense`, instead of extending `Category`.**
`CategoryExpense(categoryId, name, emoji, sortOrder, amount)` lives in `data/transaction/model/`.
The repository method `observeMonthExpensesByCategory(month: YearMonth): Flow<List<CategoryExpense>>`
maps the rows inline, like `observeMonthTotals` does.
*Alternative:* add `sortOrder` to `Category`. Rejected: it changes a model used by the transaction
list and edit screens for a field only Home needs.

**3. Total is the sum of the rows.**
The ScreenModel computes `total = categories.sumOf { it.amount }`. The center value therefore always
equals the sum of the list (spec: "Month total in the ring") and cannot disagree with the rows
during a race between two flows.
*Alternative:* reuse `observeMonthTotals(...).expense`. Rejected: a second flow and a possible
momentary mismatch.

**4. State and ScreenModel follow the list screen's Flow exception.**
`HomeState` gains `month: YearMonth` (no default, set once from the current date, as the list model
does), `categories: List<CategoryExpense>` and `total: Long`. `HomeScreenModel` collects the
repository flow in `init` inside `screenModelScope.launch` and updates only `categories` and `total`
through `mutableState.update { it.copy(...) }`; it never touches `isLoading`. `setState` does not
get parameters for these fields: they are Flow-owned, and `month` never changes, which is how
`TransactionListScreenModel.setState` treats `days`, `totals` and `currentMonth`. `isLoading` and
the existing `Idle` event stay as they are. There is no `try / catch`, as in the list model (a read
`Flow`, not a write).

**5. Shares and percents are computed in the screen, as `Double`.**
`share = amount.toDouble() / total` drives the sweep angle. The label is `share` rounded to the
nearest whole percent, or the string "<1%" when `share` is below 0.01. A percent is a ratio, not
money, so the `MoneyConverters` rule does not apply, and a `Double` avoids overflowing `Long` with
`amount * 100` on very large totals. Rounded percents can add up to 99 or 101; the spec allows it.
*Alternative:* largest-remainder rounding so rows always add to 100%. Rejected: more logic for a
cosmetic gain.

**6. Colors: a palette list in `ui/theme/Color.kt`, picked by `sortOrder`.**
`CategoryColors: List<Color>` has 10 entries; a category's color is
`CategoryColors[sortOrder % CategoryColors.size]`, computed in `HomeScreen` where slices and rows
are built so both use the same value. Default expense categories (0..8) all differ, and index 8
(the "Другое" category) is a neutral gray. Proposed values, adjustable when first seen on a screen:

| Index | Hue | Value | Default category |
|---|---|---|---|
| 0 | blue | `0xFF4E79A7` | Продукты |
| 1 | orange | `0xFFF28E2B` | Кафе и рестораны |
| 2 | coral | `0xFFE15759` | Транспорт |
| 3 | teal | `0xFF76B7B2` | Развлечения |
| 4 | green | `0xFF59A14F` | Покупки |
| 5 | yellow | `0xFFEDC948` | Здоровье |
| 6 | purple | `0xFFB07AA1` | Связь и интернет |
| 7 | pink | `0xFFFF9DA7` | Коммунальные услуги |
| 8 | gray | `0xFFBAB0AC` | Другое |
| 9 | brown | `0xFF9C755F` | (income / next custom category) |

Raw `Color(0x...)` is allowed only in `Color.kt`, never in screens. No lookup function is added:
indexing a list in one place is simpler than a helper (no new generic helpers without approval).
*Alternative A:* a `color` column on `categories`. Rejected for now (decided in exploration): it
needs a schema bump that wipes data, and a color picker for custom categories later.
*Alternative B:* color by rank in the month. Rejected: a category would change color when another
one overtakes it, breaking "Транспорт is always this color".

**7. The ring is a stroked `Canvas` arc, with the total in a `Box` on top.**
- Size about 220 dp, stroke about 28 dp, butt caps, arc bounds inset by half the stroke.
- Start angle −90° (12 o'clock); positive sweep is clockwise in Compose, matching the spec.
- Between slices a 2° gap is left only when there is more than one slice. With one slice the sweep
  is 360° and no gap is applied, so the ring is closed.
- A slice never gets less than a small minimum sweep (about 1°) so a 0.001% category stays
  visible; the cursor still advances by the true share so the circle does not drift.
- The center is a `Column` with "Всего" (`labelMedium`, `onSurfaceVariant`) and the amount
  (`titleMedium`, `onSurface`) over the `Canvas`, centered.
- The ring is not interactive and not animated.
*Alternative:* a chart library. Rejected: no new dependencies without approval, and `drawArc` is a
few lines in common code.

**8. Amounts are unsigned; the small duplicate stays private.**
Rows and the center total are `formatBalance(amount)` + a space + `strings.common.currency` in
`onSurface`, with no "+" or "−". This is a deliberate, user-approved exception to the CLAUDE.md
rule "expense `onSurface` with '−'": the breakdown contains only expenses and is headed "Расходы",
so a sign on every row adds nothing, and "−1 240 000 сум" in the ring's center would read like a
negative balance. The rule still applies to the transaction list and to any screen that mixes
income and expenses. Because no sign is added, `signedAmount` and `EXPENSE_SIGN` from the list
screen are not needed here. `currentMonth()` is `private` in `TransactionListScreenModel.kt`, so
`HomeScreenModel.kt` gets its own private one-liner instead of extracting a shared helper (needs
approval). The "%" sign is a private constant in `HomeScreen.kt`; the whole string "<1%" lives in
`HomeStrings`.

**9. Screen layout copies the list screen.**
`Scaffold` with `TopAppBar` (title `strings.home.title`, "Главная") over a `LazyColumn`:
1. a header item: `expensesTitle` and `formatMonth(state.month)`;
2. when `categories` is empty, a centered `expensesEmpty` message; otherwise the ring item and then
   `items(categories, key = { it.categoryId })`, each a `ListItem` like `TransactionRow`
   (leading: color dot + emoji; headline: name; trailing: amount over percent).

The existing `if (state.isLoading) spinner` branch is kept (it is never true today). The placeholder
"Coinly" text is replaced by this content. The `TopAppBar` is an assumption: it makes Home match the
"Операции" tab; the alternative is no bar and the header only.

**10. Strings.**
`HomeStrings` gains `expensesTitle` ("Расходы по категориям"), `expensesTotal` ("Всего"),
`expensesEmpty` ("Нет расходов за этот месяц") and `lessThanOnePercent` ("<1%"), implemented in
`Russian.kt`. The currency "сум" comes from `strings.common.currency`, as in the list screen.

## Risks / Trade-offs

- [Reordering or re-seeding categories changes `sortOrder`, which recolors them] → Acceptable for
  the MVP (the order is fixed today). The Categories feature must revisit this if it adds
  reordering, probably by storing a color then.
- [More than 10 categories repeat colors] → By design (spec: "MAY repeat"). Cannot be reproduced
  from the UI until custom categories exist, so it is confirmed by reading the code.
- [Pale hues (yellow, pink, gray) are below 3:1 contrast against a white surface] → Emoji and name
  in each row carry the identity, so color is not the only cue; darken those three values if they
  look weak when someone first runs the app.
- [A very large total (10+ digits) may not fit in the ring's center] → Center text is a single line
  at `titleMedium`; the size is chosen so about 10 digits fit, and it is not checked visually unless the app is run;
  reduce the style if it clips.
- [The empty message may flash for a frame before the first Room emission] → Same behavior as the
  transaction list; accepted.
- [The current month goes stale after midnight while the app stays open] → Inherited from the list
  screen, tracked in `transactions-mvp`; not fixed here.
- [No automated tests exist in the project] → Verification is the Android build plus
  reading the code against the specs; the app is not run (see `tasks.md`, group 5), so nothing is
  confirmed visually.

## Migration Plan

No database or dependency change: no version bump, no new files in `shared/schemas/`. Rollback is
reverting the commit. Commit only when asked.

## Open Questions

- Exact palette values: the table above is a starting point, adjusted if they look weak when
  the app is first run, without changing the specs.
