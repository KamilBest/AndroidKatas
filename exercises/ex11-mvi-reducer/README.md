# ex11 · MVI: a pure reducer

**Difficulty:** medium · **Time:** 40 min · **Category:** Architecture · MVI (reducer) · **Type:** implement

## Goal
Write the reducer of a visit booking screen (pick a doctor, then a slot, then confirm). In MVI all screen logic lives in one pure function `reduce(state, intent)`, which you test without coroutines and without mocks.

Booking a visit takes three steps: pick a doctor, pick a time slot, confirm. The screen sends *intents* (what happened, for example "doctor d1 selected"). One function, the reducer, takes the current state and an intent and returns the new state plus a list of *effects* (work to do outside, for example "fetch slots for d1"). The reducer does no I/O itself, so you test it with plain values.

Example: `SelectDoctor("d1")` gives step `PICK_SLOT`, doctor `d1`, `isLoading = true`, and the effect `FetchSlots("d1")`.

You write only the reducer, following the table below.

- **File:** `BookingReducer.kt` (types are ready in `Contract.kt`)
- **Run:** `./gradlew :ex11-mvi-reducer:test`

## Task
| Intent | New state | Effect |
|---|---|---|
| `LoadDoctors` | `isLoading`, clears `error` | `FetchDoctors` |
| `DoctorsLoaded` / `LoadFailed` | list or `error`, loading ends | – |
| `SelectDoctor(id)` | step `PICK_SLOT`, this doctor, empty slots and selection, `isLoading` | `FetchSlots(id)` |
| `SlotsLoaded(doctorId, …)` | slots, **only** for the currently selected doctor | – |
| `SelectSlot(slot)` | only a slot from the list | – |
| `Confirm` | when a slot is selected and nothing is being sent: `isSubmitting` | `Book(…)` |
| `Booked` / `BookingFailed` | sending ends (+ `error`) | `ShowConfirmation` / – |
| `Back` | from `PICK_SLOT` goes back and clears the selection | from `PICK_DOCTOR`: `Close` |

An intent that is not allowed returns the same state and no effects.

**Extra (optional):** write `BookingStore(api, scope)` with `dispatch(intent)`: it calls the reducer, exposes a `StateFlow` and runs the effects (`FetchDoctors` → API → `DoctorsLoaded`).

## Background
- MVI = Model-View-Intent, a unidirectional data flow: the UI sends intents, the reducer returns a new state plus effects, the UI renders the state.
- The reducer is pure: no `suspend`, no scopes, no I/O. Effects are only *data* that someone else (a store) executes. Same pattern as the claim state machine in ex01.
- Race protection is a condition in the reducer: ignore a late `SlotsLoaded` for another doctor, ignore a second `Confirm` while submitting.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
data class CounterState(val count: Int = 0, val isSaving: Boolean = false)

sealed interface CounterIntent {
    data object Increment : CounterIntent
    data object Save : CounterIntent
}

sealed interface CounterEffect {
    data class Persist(val count: Int) : CounterEffect
}

data class Next(val state: CounterState, val effects: List<CounterEffect> = emptyList())

fun reduce(state: CounterState, intent: CounterIntent): Next = when (intent) {
    CounterIntent.Increment -> Next(state.copy(count = state.count + 1))
    CounterIntent.Save ->
        if (state.isSaving) Next(state)   // not allowed: same state, no effects
        else Next(state.copy(isSaving = true), listOf(CounterEffect.Persist(state.count)))
}
```

- [Android: UI layer and unidirectional data flow](https://developer.android.com/topic/architecture/ui-layer)
- [Kotlin: data classes and copy](https://kotlinlang.org/docs/data-classes.html#copying)
