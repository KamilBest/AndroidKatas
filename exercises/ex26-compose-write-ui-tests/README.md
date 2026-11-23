# ex26 · Write Compose UI tests

**Difficulty:** medium · **Time:** 40 min · **Category:** Compose · UI tests · **Type:** write tests

## Goal
Write UI tests for a ready refund claim form.

- **File:** `ClaimFormTest.kt` (the form in `ClaimForm.kt` is ready, don't change it)
- **Run:** `./gradlew :ex26-compose-write-ui-tests:testDebugUnitTest`

## Task
The form:
- **Amount** (`testTag("amount")`): accepts `123,45` and `123.45`, range 0,01–10 000 zł; the error "Enter an amount from 0,01 to 10 000 zł" appears only after a wrong value is typed.
- **Description** (`testTag("description")`): max 200 characters (longer text is cut), counter `"N/200"`.
- Category: chips "Doctor", "Drugs", "Rehab" (one of them).
- **"Submit claim"** is enabled when the amount is valid, a category is selected and the description is not empty → `onSubmit(ClaimDraft)`.

Write at least 7 tests: initial state, amount error and its disappearance, comma (`"123,45"` → `12345`), amount boundaries, description counter and cutting, exact data in `onSubmit` (description after `trim()`).

**Extra (optional):** write all tests through a Robot, and the amount boundaries as one loop over (value, expected result) pairs.

## Background
- `createComposeRule()` + `setContent { … }` renders the composable; tests find nodes in the semantics tree.
- Finders: `onNodeWithTag`, `onNodeWithText`. Actions: `performTextInput`, `performTextReplacement`, `performClick`. Assertions: `assertIsEnabled`, `assertIsNotEnabled`, `assertDoesNotExist`, `assertTextContains`.
- To test a callback, capture its value in a `var` (`onSubmit = { submitted = it }`).
- The test rule waits for Compose to be idle by itself, so you never need `Thread.sleep`.
- A Robot (page object) hides the "how" behind methods like `enterAmount(...)`, so tests read like scenarios.
