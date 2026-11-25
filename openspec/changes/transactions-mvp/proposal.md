# Proposal

## Why

The transactions MVP (a month list and an add / edit screen) is already built, but it is described
nowhere except in code, and the code was written by an LLM. There is no written statement of what
the app is supposed to do, so there is nothing to check it against and nothing to build Limits,
Savings and Statistics on top of. This change writes down the behavior that exists today.

## What Changes

- Add a written spec for the transaction list screen: month navigation, grouping by day, month
  totals, empty state.
- Add a written spec for the transaction add / edit / delete screen: fields, validation, date
  picker, delete confirmation, error handling.
- Check every scenario against the running app, and correct any spec wording that turns out to be
  wrong.
- No behavior change and no code change. Every requirement describes what the app already does.
  Anything the specs reveal as wrong or missing is recorded as a note, not fixed here.

## Capabilities

### New Capabilities

- `transaction-list`: browsing transactions of one calendar month, newest first and grouped by day,
  with the month's income and expense totals and navigation between months.
- `transaction-edit`: creating, editing and deleting a single transaction, including input rules,
  validation and the seeded default categories it picks from.

### Modified Capabilities

None. There are no existing specs.

## Impact

- Code: none.
- Dependencies, APIs, database schema: none.
- Documentation: adds `openspec/specs/transaction-list` and `openspec/specs/transaction-edit` once
  archived.
- Out of scope: custom categories, limits, savings, statistics, the Home tab, and the small issues
  noticed while reading the code (no autofocus on the amount field, the last used category is not
  remembered, "today" and the current month are computed once and go stale after midnight, no
  automated tests, and editing a transaction that no longer exists shows "Операция не найдена" but
  leaves an empty form whose "Сохранить" updates nothing and still closes the screen as if it had
  worked; found by reading the code, not reproduced).
