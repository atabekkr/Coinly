# Coinly — visual design handoff

Design reference for four screens, drawn on a design canvas on 2026-11-15. This folder is
reference material, not a task list.

- `screens/*.png` — how each screen looks (390 × 844 dp frame, rendered at 2x).
- `source/*.html` — the screens as HTML with inline styles. Read them for exact values (sizes,
  colors, radii, icon paths). They are static mockups, not code to port line by line.
- This file — tokens, components, per-screen layout, and where the design differs from the project.

## How to use this

1. `CLAUDE.md` wins. Where this design conflicts with a rule or an agreed decision there, follow
   `CLAUDE.md` and ask Atabek — do not resolve the conflict yourself. The known conflicts are listed
   in "Differences from the project" below.
2. Build only what Atabek asks for, one screen or feature at a time. Nothing here is a request to
   build a screen, change the theme or add a tab.
3. Everything shown as content is sample data: English labels, USD amounts with cents, category
   names, numbers, dates and times. Take real text from `ui/localization/`, real amounts through
   `MoneyConverters`, real categories from the database.
4. Sizes are CSS px at a 390 px wide frame; read them as dp.

## Design → project mapping

| Design | Project |
|---|---|
| Home (`home.png`) | "Главная" tab — MVP feature 5 (balance, month income / expenses, recent transactions). Not built yet: today `HomeScreen` shows the expense ring and list. |
| Add transaction (`add-transaction.png`) | `screen/transaction/edit/` (exists; add and edit mode). |
| Report (`report.png`) | Expenses of a month by category: ring + list. This is what `HomeScreen` shows today, and MVP feature 6 "Statistics". |
| Budgets (`budgets.png`), "Categories" block | MVP feature 3 "Limits" ("Лимиты"). Not built. |
| Budgets, "Goals" block | MVP feature 4 "Savings" ("Копилки" with a goal amount). Not built. |
| Bottom bar: Home / Report / + / Budgets / Settings | Today: tabs "Главная" and "Операции" in `MainScreen`. |

## Differences from the project (ask before deciding)

Each item is a place where the picture and `CLAUDE.md` disagree, or where the picture shows
something the project has no rule for.

**Look and theme**

- The design is a custom look (own colors, two fonts, large radii). `CLAUDE.md` says: no custom
  design system yet, standard Material 3, no `Color(0x...)` in screens. Applying this look means a
  theme change (color scheme, typography, shapes in `ui/theme/`) that needs explicit approval.
- Fonts: Space Grotesk (headings, amounts) and Inter (everything else). Neither is in the project.
  Adding font files is a new resource and needs approval. Check that the chosen files cover
  Cyrillic before using them for Russian text.
- Only a light theme was designed.

**Content rules**

- Language and currency: the mockups are English and USD with cents. The project is Russian, UZS,
  whole sums on input, formatting only through `MoneyConverters`.
- Category icons: the design draws line icons in tinted tiles. The project uses emoji. The tile
  (rounded square with a tint) works with an emoji inside.
- Category colors in charts: the design uses its own five colors. The project rule is
  `CategoryColors[sortOrder % size]`.
- Income green: design `#0B7A55`, project `IncomeColor` (`0xFF2E7D32`).
- Transaction rows show a time ("18:42") under the category name. Transactions store a date
  without time, so there is no time to show. What goes on the second line (note, nothing) is
  undecided.

**Navigation**

- The design has four tabs plus a centre "+" button: Home, Report, Budgets, Settings. The project
  has "Главная" and "Операции"; "Лимиты" and "Копилки" come with their features. There is no
  decision on a Report tab, a Settings tab, or on where the "Операции" list goes in this layout.
- The bar with a notch around the "+" button is not the Material 3 `NavigationBar`; it needs a
  custom composable in `MainScreen`.
- The "+" button opens the add-transaction screen.

**Home**

- Total balance needs a starting balance. That rule is still open in `CLAUDE.md` ("Initial
  balance").
- The profile avatar and the notification bell have no feature behind them (no auth, notifications
  are out of MVP).
- "+$1,554.70 this month" is month income minus month expenses.

**Add transaction**

- The design has its own on-screen keypad with a "." key and an amount with cents. The project
  takes whole sums (digits only, at most 12) from the system keyboard with
  `AmountVisualTransformation`.
- The category grid shows 8 tiles, the last one "More". The project has 9 default expense
  categories and 3 income ones; how the grid behaves with more categories is undecided.
- Edit mode (delete button) was not drawn.

**Report**

- The Expenses / Income switch: the project's statistics are expenses only.
- "Daily average" and "Top category" are new figures with no rule in the project.
- The bar under each category is scaled to the biggest category (biggest = full width), not to
  100% of the total.

**Budgets**

- The top card shows one total monthly budget ("$1,354.70 left of $4,000"). The project has only
  per-category limits; whether a total exists and how it is calculated is undecided.
- States in the mockup are illustrative: 82% is drawn as normal and 99% as warning. The project
  rule is warning color at 80%, red at 100% and above.

## Tokens

### Color

| Role | Hex | Used for |
|---|---|---|
| Background | `#F3F2F7` | Screen ground |
| Surface | `#FFFFFF` | Cards, bottom bar, keypad keys, round icon buttons |
| Text | `#16132B` | Primary text, expense amounts |
| Text secondary | `#5F5B73` | Labels, secondary lines, inactive tabs |
| Divider | `#ECEAF3` | Lines between rows inside a card, neutral bar track |
| Border / track | `#E6E4EE` | Segmented control track, bottom bar outline, backspace key |
| Primary | `#6246E0` | Hero, "+" button, primary button, active tab, links, selected outline |
| Primary tint | `#ECE8FC` | Bar track for a normal budget, violet icon tile |
| On primary | `#FFFFFF` | Text and icons on Primary |
| On primary, secondary | `#E7E2FF` | Labels on Primary ("Total balance") |
| On primary, cents | `#CFC6FF` | Cents part of the big balance |
| Overlay on primary | `rgba(22,19,43,0.22)` | Pills and round buttons on the hero (`0.28` for the bar track) |
| Cents on background | `#6E6A85` | Cents part of the entered amount |
| Income | `#0B7A55` | Income amounts, goal progress |
| Income tint | `#DFF3EA` | Income icon tile, goal bar track |
| Warning text | `#B4500F` | "left" text near the limit, expense arrow |
| Warning bar | `#E8823A` | Bar near the limit |
| Warning tint | `#FCE9DC` | Warning bar track, orange icon tile |
| Danger | `#B42318` | Over-limit bar and text |
| Danger tint | `#FBE3E0` | Over-limit bar track |

Icon tiles (background / icon): blue `#E3EEFC` / `#1F5FBF`, orange `#FCE9DC` / `#B4500F`,
violet `#ECE8FC` / `#6246E0`, teal `#DDF3F0` / `#0F766E`, pink `#FCE4EC` / `#B4235A`,
yellow `#FBF0CF` / `#8A5A00`, green `#DFF3EA` / `#0B7A55`, gray `#ECEAF3` / `#5F5B73`.

Chart colors in the mockup: `#2F7DE1`, `#6246E0`, `#1E9E8A`, `#E8823A`, `#B9B6C9` (see the note on
`CategoryColors` above).

### Type

Space Grotesk — headings and all money figures:

| Style | Size / line | Weight |
|---|---|---|
| Entered amount | 52 / 56, tracking −0.02em | 700 |
| Total balance | 44 / 52, tracking −0.02em | 700 |
| Budget hero amount | 32 / 38, tracking −0.02em | 700 |
| Screen title | 26 / 32, tracking −0.01em | 700 |
| Summary card amount | 22 / 28 | 600 |
| Keypad digit | 22 | 500 |
| Section heading, stat value, top bar title | 18 / 24 | 600 |
| Row amount | 16 (15 in Report and Budgets rows) | 600 |

Inter — everything else:

| Style | Size | Weight |
|---|---|---|
| Primary button | 16 | 600 |
| Row title | 15 | 600 |
| Pill, field button, segmented control, link | 14 | 500 (600 selected) |
| Secondary line, card label | 13 | 400–500 |
| Category tile label, percent, "left" text | 12 | 500 (600 selected / warning / danger) |
| Tab label | 11 | 500 (600 active) |

### Shape

| Element | Radius |
|---|---|
| Hero bottom corners | 32 |
| Chart card, budget hero card | 24 |
| List cards, summary cards, goal card | 20 |
| Primary button | 18 |
| Segmented control track / segment, category tile | 16 / 12, 16 |
| Keypad key, field button | 14 |
| Icon tile 36–40 / 28 | 12 / 10 |
| Pills, round icon buttons, "+" button | fully round |
| Progress bars | fully round; height 6 (10 in the budget hero) |

### Spacing and size

- Screen side padding 20. Card inner padding 16 (20 in the budget hero).
- Content starts 56 below the top of the frame. That is status bar space plus about 12; in the app
  use system insets instead of a fixed number.
- Between sections 20–24; heading to its card 12; between cards in a grid 8–12.
- Touch targets are at least 44: round buttons 44, pills 44, segments 44, keypad keys 48, field
  buttons 48, primary button 56.
- Row heights: transaction 64, report category 58, budget category 66.

### Shadow

Cards are flat on the background except:

- summary cards overlapping the hero: `0 6 18 rgba(22,19,43,0.06)`;
- selected segment: `0 2 6 rgba(22,19,43,0.08)`;
- "+" button: `0 8 20 rgba(98,70,224,0.35)`.

## Components

**Bottom bar with notch** (Home, Report, Budgets)

- Height 84 in the frame = 64 of bar + 20 of bottom inset. White, 1 px outline `#E6E4EE` on the top
  edge only.
- Top edge has a smooth round dip in the centre. Path in a 390 × 84 box:
  `M-2 1 H143 C155 1 159 9 162 17 C167 31 180 41 195 41 C210 41 223 31 228 17 C231 9 235 1 247 1 H392 V86 H-2 Z`
  (dip is 104 wide at the top and 40 deep).
- "+" button: 56 circle, Primary, white plus icon 26; centred horizontally; its top is 24 above the
  bar's top edge, leaving about 8 of gap to the dip.
- Five equal columns: tab, tab, "+", tab, tab. Tab = icon 24 above an 11 label, gap 4. Active tab:
  Primary color, label weight 600. Inactive: Text secondary.

**Segmented control** — track `#E6E4EE`, radius 16, padding 4, segments 44 high with radius 12 and
gap 4. Selected: white, shadow, Text, 600. Unselected: transparent, Text secondary, 500.

**Icon tile** — rounded square with a tint and an 18–20 icon: 40 in transaction rows, 36 in report /
budget rows and category tiles, 28 in the Income / Expenses cards.

**List card** — white, radius 20, side padding 16, rows separated by 1 px `#ECEAF3` (no line after
the last row).

**Transaction row** — height 64: tile 40, gap 12, title 15/600 over a 13 secondary line, amount
16/600 on the right. Income: Income color with "+". Expense: Text with "−".

**Progress row** (Report, Budgets) — tile 36, then two lines: title left and amount right; bar
(height 6) with a short text on its right (percent, 30 wide, in Report; "left / over" text, 92 wide,
in Budgets). Gap between the lines 6.

**Pill** — height 44, fully round, side padding 16, 14/500 text with a 16 chevron. On the hero:
overlay background, white text. On the background: white.

**Primary button** — height 56, radius 18, Primary, white 16/600.

## Screens

### Home — `screens/home.png`, `source/Home.html`

1. Hero, Primary, full width, bottom corners 32, padding 56 / 20 / 72.
   - Row: avatar (44, white circle, initial in Primary), month pill, bell (44, overlay circle).
   - 28 below, centred: "Total balance" 14/500; balance 44/52 with the cents in `#CFC6FF`; chip
     (13/500, overlay background, padding 5 × 12, radius 14) with the month's change.
2. Two summary cards in a row (gap 12, side margin 20), pulled up 44 over the hero. Each: white,
   radius 20, padding 14 × 16, shadow; a 28 tile with an arrow (income: green, down-left; expenses:
   orange, up-right) and a 13 label; amount 22/600 below.
3. "Transactions" heading (18/600) with a "See all" link on the right; 24 above, 12 below.
4. Day groups: a 13/500 secondary label ("Today, 15 Nov"), 8 below it a list card of transaction
   rows; 16 between groups.
5. Bottom bar, Home active.

### Add transaction — `screens/add-transaction.png`, `source/AddTransaction.html`

Full screen over the tabs, no bottom bar. Side padding 20, bottom padding 16.

1. Top bar, height 44: close button (44, white circle, X icon) on the left, title 18/600 centred.
2. 16 below: segmented control, two segments — Expense, Income.
3. Amount, centred in the remaining free space: 52/56, sign in front, cents in `#6E6A85`.
4. Category grid: 4 columns, gap 8. Tile: white, 72 high, radius 16; a 36 icon tile over a 12
   label. Selected: 2 px Primary outline, label Text / 600. Others: label Text secondary / 500.
5. 12 below: two field buttons side by side (gap 8), 48 high, white, radius 14: date (calendar
   icon, "Today, 15 Nov") and note (pencil icon, "Add note" in Text secondary while empty).
6. 12 below: keypad, 3 columns, gap 8, keys 48 high, white, radius 14, digits 22/500. Last row:
   ".", "0", backspace (key in `#E6E4EE`).
7. 12 below: primary button "Save expense".

### Report — `screens/report.png`, `source/Report.html`

1. Header, height 44: title 26/700 on the left, month pill (white) on the right.
2. 16 below: segmented control — Expenses, Income.
3. 16 below: chart card, white, radius 24, padding 16, height 200.
   - Left: ring 168 across, stroke about 20 (24 in a 200 box, radius 80), small gaps between
     segments, starting at 12 o'clock and going clockwise. In the centre: "Total spent" 12/500
     secondary over the total 18/700.
   - Right, 16 away: two stats stacked with a 16 gap, each a 12/500 secondary label over an 18/600
     value.
4. "By category" heading; 20 above, 12 below.
5. List card of progress rows, biggest first: name, amount, bar in the category color, percent.
6. Bottom bar, Report active.

### Budgets — `screens/budgets.png`, `source/Budgets.html`

1. Header, height 44: title 26/700; add button (44, white circle, plus icon) on the right.
2. 16 below: hero card, Primary, radius 24, padding 20, 14 between its three lines:
   - label 13/500 in `#E7E2FF`; under it the amount left 32/700 and "left of …" 14/500 on one
     baseline;
   - bar, height 10: track `rgba(22,19,43,0.28)`, fill white;
   - "66% spent" on the left, "15 days left" on the right, 13/500.
3. "Categories" heading; 20 above, 12 below.
4. List card of progress rows: name; "spent of limit" on the right (spent 15/600 in Text, the rest
   13 secondary); bar; state text on the right of the bar.
   - Normal: bar Primary on Primary tint; "… left" in Text secondary.
   - Warning: bar `#E8823A` on Warning tint; "… left" in Warning text, 600.
   - Over limit: full bar in Danger on Danger tint; "Over by …" in Danger, 600.
5. "Goals" heading with a "See all" link; 20 above, 12 below.
6. Goal card: white, radius 20, padding 16. Row: tile 36, name 15/600 over "saved of goal" 13
   secondary, percent 18/600 in Income on the right. 12 below: bar, height 6, Income on Income
   tint.
7. Bottom bar, Budgets active.

## About the files

- The PNGs were rendered from the `source/` files in headless Chromium with Space Grotesk and Inter
  loaded from local font files.
- The `source/` files load both fonts from Google Fonts, so opened offline they fall back to a
  system font.
- The bottom-bar links in `source/` point at the canvas file names (`Main.dc.html` and so on), so
  they do not navigate here.
