# ex15 · `SavedStateHandle`: a form that survives process death

**Difficulty:** hard · **Time:** 45 min · **Category:** Android · ViewModel and process death · **Type:** implement

## Goal
Write the ViewModel of a visit form that comes back filled in after the system kills the app.

- **File:** `VisitForm.kt`
- **Run:** `./gradlew :ex15-savedstate-viewmodel:testDebugUnitTest`

## Task
1. `doctorId` comes from the navigation arguments (`savedStateHandle["doctorId"]`); missing → `IllegalStateException`.
2. Fields `symptoms`, `sinceDays: Int?`, `urgent` live in `SavedStateHandle` (`getStateFlow` + write).
3. `state`: the fields + validation (`symptoms` ≥ 10 characters after `trim`, `sinceDays` in 0..365) + `isSubmitting`. An empty form shows no errors.
4. `isSubmitting` and the submit result are **not** in the handle.
5. `submit()`: only when the form is valid and nothing is running; on success clear the fields in the handle and send `Submitted(visitId)`; on error send `Error(message)`.

**Extra (optional):** explain in a comment why you don't keep the list of symptom suggestions from the API in the handle (~1 MB limit, `TransactionTooLargeException`).

## Background
- A rotation keeps the ViewModel. Process death kills it; only `SavedStateHandle` (and `rememberSaveable`) come back, restored from a `Bundle`.
- `handle.getStateFlow(key, initial)` gives a `StateFlow`, `handle[key] = value` writes. The handle is the single source of truth for the fields.
- Keep only small, serializable values there. A running request cannot be resumed after process death, so don't save it.
- Navigation puts route arguments into the same `SavedStateHandle`. A "process death" test = a new ViewModel with the same handle.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
class SearchViewModel(private val handle: SavedStateHandle) : ViewModel() {
    // Throws IllegalStateException when the argument is missing
    private val userId: String = checkNotNull(handle["userId"]) { "userId is missing" }

    val query: StateFlow<String> = handle.getStateFlow("query", "")
    fun onQueryChange(value: String) {
        handle["query"] = value
    }

    private val isLoading = MutableStateFlow(false)   // not in the handle

    val state: StateFlow<SearchState> = combine(query, isLoading) { q, loading ->
        SearchState(q, loading)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SearchState())
}

// "Process death" in a test: a new ViewModel with the same handle
val handle = SavedStateHandle(mapOf("userId" to "u1"))
```

- [Android: Saved State module for ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-savedstate)
- [Android: ViewModel overview](https://developer.android.com/topic/libraries/architecture/viewmodel)
