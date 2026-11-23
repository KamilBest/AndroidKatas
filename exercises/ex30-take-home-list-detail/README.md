# ex30 · Mini take-home: prescription list and details

**Difficulty:** hard · **Time:** 50 min · **Category:** Practice · take-home task · **Type:** implement

## Goal
Write the ViewModels of a prescription list and its details screen: mapping, screen states and error handling. The repository is ready (an interface). Treat it as a take-home task with a 50-minute timer.

- **File:** `ViewModels.kt` (domain and screen states in `Domain.kt`)
- **Run:** `./gradlew :ex30-take-home-list-detail:testDebugUnitTest`
- **Small steps:** `StepsTest.kt` tests single building blocks, start there: `./gradlew :ex30-take-home-list-detail:testDebugUnitTest --tests '*StepsTest'`

## Task
1. `toItem(clock)`: title `"Amotaks 1000 mg"`, subtitle `"Valid until 12.03.2026"`, badge: redeemed → `REDEEMED`; the date has passed (today in `Europe/Warsaw`) → `EXPIRED`; otherwise `ACTIVE`.
2. `PrescriptionListViewModel`:
   - refresh on start; `Loading` until the first refresh ends,
   - then `Content` (active ones from the nearest date, then redeemed, then expired) or `Empty`,
   - refresh error with an empty database → `Error("Could not load prescriptions")`,
   - error with data in the database → `Content` stays + message `"No connection, showing saved data"`,
   - `onRefresh()` → `isRefreshing = true` while it runs.
3. `PrescriptionDetailViewModel`: `id` from `SavedStateHandle["id"]`, state `Found(item)` or `NotFound`.

**Extra (optional):** add the list screen in Compose (card, badge, pull-to-refresh, snackbar) and 2 UI tests.

## Background
- The screen state is computed from two sources: data from the database and the refresh state. `combine` + `stateIn` joins them.
- A sealed `UiState` (`Loading`, `Content`, `Empty`, `Error`) instead of several boolean flags that can contradict each other.
- "No data" and "old data" are different errors: the first gets a full-screen error, the second a message.
- Navigation puts arguments into `SavedStateHandle`, so the details ViewModel reads `id` from there.
