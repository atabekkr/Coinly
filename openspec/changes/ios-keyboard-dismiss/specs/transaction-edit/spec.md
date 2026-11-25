# Spec Delta

## Purpose

Lets the user record a new income or expense, correct an existing one, or delete it, with input rules and validation that keep every saved transaction complete and sensible.

## ADDED Requirements

### Requirement: On-screen keyboard can be dismissed
While the on-screen keyboard is open on the add / edit screen, the user SHALL be able to close it without leaving the screen by tapping an empty area, a category chip, the date field or "Сохранить". Closing the keyboard SHALL keep everything already entered, and tapping a field SHALL open it again.

#### Scenario: Amount keyboard
- **WHEN** the keyboard is open for "Сумма" and has no Return key, and the user taps an empty area of the screen
- **THEN** the keyboard closes
- **AND** the typed amount is still shown

#### Scenario: Note keyboard
- **WHEN** the keyboard is open for "Комментарий" and the user taps an empty area of the screen
- **THEN** the keyboard closes
- **AND** the typed note is still shown

#### Scenario: Category chip
- **WHEN** the keyboard is open and the user taps the chip "🍽 Кафе и рестораны"
- **THEN** the keyboard closes
- **AND** that chip is selected

#### Scenario: Date field
- **WHEN** the keyboard is open and the user taps the date field
- **THEN** the keyboard closes
- **AND** the date picker opens

#### Scenario: Save button
- **WHEN** the keyboard is open and the user taps "Сохранить"
- **THEN** the keyboard closes
- **AND** the form is validated and saved as it would be without the keyboard

#### Scenario: Opening the keyboard again
- **WHEN** the keyboard has been closed and the user taps "Сумма"
- **THEN** the keyboard opens again with the amount still in the field
