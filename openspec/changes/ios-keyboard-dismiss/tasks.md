# Tasks

The only code file touched is `TransactionEditScreen.kt`. Checks are by hand: the project has no UI test setup, and adding one needs approval.

## 1. Reproduce

- [ ] 1.1 Reproduce the bug before changing code: run the `iosApp` scheme on an iPhone simulator, turn off I/O > Keyboard > Connect Hardware Keyboard (Shift+Cmd+K) so the software keyboard shows, open "Добавить операцию" and tap "Сумма"; verify the number pad has no Return or Done key and tapping an empty area leaves it open. If the keyboard already closes, pause and revisit the plan instead of continuing

## 2. Dismiss the keyboard

- [x] 2.1 In `TransactionEditScreen.Content()` take the screen's focus manager and make a tap on the scrollable form column clear focus, inline in the screen with no shared helper; verify `./gradlew :shared:compileKotlinIosSimulatorArm64` succeeds
- [x] 2.2 Also clear focus in the category chip `onClick`, in the date field's press-release handler, and in the "Сохранить" `onClick`, keeping each existing event call; verify `./gradlew :androidApp:assembleDebug` succeeds and `git diff --stat -- shared` lists only `TransactionEditScreen.kt`

## 3. Verify

- [ ] 3.1 On the iOS simulator with the software keyboard on, check every scenario of "On-screen keyboard can be dismissed": empty-area tap after "Сумма", empty-area tap after "Комментарий", chip tap with the keyboard open, date field tap, "Сохранить" tap, and tapping "Сумма" again; verify the keyboard closes each time, typed values stay, the chip becomes selected, the date picker opens, the save runs as before and the keyboard reopens
- [x] 3.2 On the Android emulator, repeat the same scenarios as a regression check; verify the number keyboard's own key still closes it, the form still scrolls, chips still select and "Сохранить" still saves

## Workflow follow-up

- Verify on the physical iPhone once it can be signed and installed; signing is not set up yet and is outside this change.
- Archive the change. `transactions-mvp` also introduces `transaction-edit`, so archive them one after the other; the second one merges its requirements into the spec the first one created.
