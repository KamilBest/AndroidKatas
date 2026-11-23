# ex10 · ViewModel: one UiState and one-off events

**Difficulty:** hard · **Time:** 45 min · **Category:** Architecture · MVVM · **Type:** implement

## Goal
Write the ViewModel of a list screen: loading, refresh, a full-screen error or a snackbar, and a click that opens details.

- **File:** `ClaimsViewModel.kt` (the model and `formatPln` are ready in `Claims.kt`)
- **Run:** `./gradlew :ex10-mvvm-viewmodel:testDebugUnitTest`

## Task
1. On start, observe `repository.observeClaims()` and call `refresh()`. During the first load `isLoading = true`.
2. When `refresh()` fails:
   - the list is empty → `error = "No connection"` (error screen with a Retry button),
   - the list is not empty → event `ShowSnackbar("Could not refresh")`, the data stays.
3. `onRefresh()` and `onRetry()` → `refresh()`. Pull-to-refresh sets `isRefreshing = true` while it runs.
4. `onClaimClicked(id)` → event `OpenDetails(id)`. Events must not get lost or arrive twice.

**Extra (optional):** build the state without `init { launch }`, only with `combine(...).stateIn(viewModelScope, WhileSubscribed(5_000), …)`, and write in a comment what you gain and what you lose.

## Background
- One immutable `UiState` exposed as `StateFlow`, changed with `_state.update { it.copy(...) }` (atomic, no race).
- One-off events (navigation, snackbar) go through `Channel(BUFFERED)` + `receiveAsFlow()`: the channel keeps an event while nobody listens (for example during rotation) and delivers it once.
- In tests `MainDispatcherRule` replaces `Dispatchers.Main`, and `advanceUntilIdle()` finishes pending coroutines.
