# Proposal

## Why

On iOS, once the user has typed into the add / edit transaction form, the on-screen keyboard cannot be closed. The amount field uses the iOS number pad, which has no Return or Done key. The note field is multi-line, so its Return key adds a new line. Nothing in the app clears focus when the user taps elsewhere, so the keyboard stays up and covers the form. This was found by reading the code and is confirmed by the first task before anything is changed. It blocks using the app on a real iPhone.

## What Changes

- Tapping an empty area of the add / edit transaction screen closes the on-screen keyboard.
- Tapping a category chip, the date field or "Сохранить" also closes the keyboard, while doing what it did before.
- Everything already typed is kept when the keyboard closes.
- The fix lives in the screen itself, with no shared helper, no new text and no new dependency. It takes effect on Android as well, where the keyboard could previously only be closed with its own key or the back gesture.

## Capabilities

### New Capabilities

- `transaction-edit`: adds one requirement, that the on-screen keyboard can be dismissed on the add / edit screen. The capability as a whole is introduced by the unarchived `transactions-mvp` change; whichever of the two changes is archived second merges its requirements into the capability the first one created.

### Modified Capabilities

None. There are no archived specs yet.

## Impact

- Code: `TransactionEditScreen.kt` only, in the shared module, so both platforms.
- Strings, database, dependencies, navigation: none.
- Out of scope: a visible "Готово" bar above the keyboard (needs a new string and iOS-specific code), making the note single-line or changing what its Return key does, other screens (the transaction list has no text fields), whether the keyboard covers lower fields (not investigated), and signing the app for the physical iPhone.
