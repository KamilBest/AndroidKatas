# ex07 · Flow: search with debounce, and callbackFlow

**Difficulty:** hard · **Time:** 45 min · **Category:** Flow · operators · **Type:** implement

## Goal
Build a drug search that does not call the API on every key press, and turn a callback sensor API into a `Flow`.

- **File:** `Flows.kt`
- **Run:** `./gradlew :ex07-flow-operators:test`
- **Small steps:** `StepsTest.kt` tests single building blocks, start there: `./gradlew :ex07-flow-operators:test --tests '*StepsTest'`

## Task
1. `DrugSearch.state`: a drug search as `StateFlow<SearchState>`:
   - wait 300 ms after the last key press, trim spaces,
   - a query shorter than 2 characters → `Idle` (no API call),
   - the same query twice → do nothing,
   - a new query cancels the previous request,
   - for a query: `Loading`, then `Results` or `Error`; an error does not stop the search.
2. `HeartRateSensor.readings()`: a `callbackFlow` that registers the listener and unregisters it when nobody collects any more.

**Extra (optional):** add `retryWhen` that retries the query once after 1 s on `IOException`, with a virtual-time test.

## Background
- `flatMapLatest` cancels the previous inner flow when a new value arrives.
- `catch` only catches errors from upstream. Put it on the outer flow and the first error ends the whole search, so it belongs inside `flatMapLatest`.
- `stateIn(scope, WhileSubscribed(5_000), initial)` turns a cold flow into a hot `StateFlow`; the 5 s keep it alive during a rotation.
- `callbackFlow` needs `awaitClose { unregister }`, otherwise the flow ends at once or the listener leaks.
