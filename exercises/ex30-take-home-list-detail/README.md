# ex30 · Mini take-home: prescription list and details

**Difficulty:** hard · **Time:** 50 min · **Category:** Practice · take-home task · **Type:** implement

## Goal
Write the ViewModels of a prescription list and its details screen: mapping, screen states and error handling. The repository is ready (an interface). Treat it as a take-home task with a 50-minute timer.

A small list-and-details app, like a typical take-home task: you get a repository and the screen models, and you write the logic in between.

The list ViewModel turns prescriptions into display items (a title, "Valid until …", a badge), sorts them, and handles loading, an empty list, and errors with or without saved data. The details ViewModel shows one prescription by its id from the navigation arguments.

Example: a prescription valid until yesterday and not redeemed gets the badge `EXPIRED`.

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

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
sealed interface ScreenState {
    data object Loading : ScreenState
    data class Content(val items: List<String>) : ScreenState
    data object Empty : ScreenState
}

val state: StateFlow<ScreenState> = combine(repo.observeAll(), isRefreshing) { items, refreshing ->
    if (items.isEmpty()) ScreenState.Empty else ScreenState.Content(items)
}.stateIn(viewModelScope, SharingStarted.Eagerly, ScreenState.Loading)

LocalDate.of(2026, 3, 12).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))   // "12.03.2026"
val today = LocalDate.now(clock.withZone(ZoneId.of("Europe/Warsaw")))
date.isBefore(today)

list.sortedWith(compareBy<Item> { it.group }.thenBy { it.date })

val id: String = checkNotNull(savedStateHandle["id"])
```

- [Android: UI layer state holders](https://developer.android.com/topic/architecture/ui-layer/stateholders)
- [Android: UI events](https://developer.android.com/topic/architecture/ui-layer/events)
- [Android: guide to app architecture](https://developer.android.com/topic/architecture)
