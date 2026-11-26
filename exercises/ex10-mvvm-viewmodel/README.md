# ex10 · ViewModel: one UiState and one-off events

**Difficulty:** hard · **Time:** 45 min · **Category:** Architecture · MVVM · **Type:** implement

## Goal
Write the ViewModel of a list screen: loading, refresh, a full-screen error or a snackbar, and a click that opens details.

A list of claims with everything a real screen has: a first load with a spinner, pull-to-refresh and errors. The data comes from a repository: `observeClaims()` streams what is stored, and `refresh()` gets new data from the network.

The ViewModel turns this into one `UiState` for the screen, plus one-time events (open details, show a snackbar) that must happen exactly once, also around a screen rotation.

Example: no data and no network shows a full-screen error with Retry. Old data and no network keeps the list and shows the snackbar "Could not refresh".

- **File:** `ClaimsViewModel.kt` (the model and `formatPln` are ready in `Claims.kt`)
- **Run:** `./gradlew :ex10-mvvm-viewmodel:testDebugUnitTest`

## Task
1. On start, observe `repository.observeClaims()` and call `refresh()`. During the first load `isLoading = true`.
2. When `refresh()` fails:
   - the list is empty → `error = "No connection"` (error screen with a Retry button),
   - the list is not empty → event `ShowSnackbar("Could not refresh")`, the data stays.
3. `onRefresh()` and `onRetry()` → `refresh()`. Pull-to-refresh sets `isRefreshing = true` while it runs.
4. `onClaimClicked(id)` → event `OpenDetails(id)`. Events must not get lost or arrive twice.

**Extra (optional):** build the state without `init { launch }`, only with `combine(...).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue)`, and write in a comment what you gain and what you lose.

## Background
- One immutable `UiState` exposed as `StateFlow`, changed with `_state.update { it.copy(...) }` (atomic, no race).
- One-off events (navigation, snackbar) go through `Channel(BUFFERED)` + `receiveAsFlow()`: the channel keeps an event while nobody listens (for example during rotation) and delivers it once.
- In tests `MainDispatcherRule` replaces `Dispatchers.Main`, and `advanceUntilIdle()` finishes pending coroutines.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
class ProfileViewModel(private val repo: ProfileRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events: Flow<ProfileEvent> = _events.receiveAsFlow()

    fun onSave() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }   // atomic read-modify-write
            _events.send(ProfileEvent.Saved)
        }
    }
}

// Tests: Dispatchers.Main does not exist on the JVM
Dispatchers.setMain(UnconfinedTestDispatcher())   // @Before (MainDispatcherRule does this)
Dispatchers.resetMain()                           // @After
advanceUntilIdle()                                // inside runTest: run pending coroutines
```

- [Android: ViewModel overview](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Android: UI events](https://developer.android.com/topic/architecture/ui-layer/events)
- [Android: testing coroutines](https://developer.android.com/kotlin/coroutines/test)
