# ex07 · Flow: search with debounce, and callbackFlow

**Difficulty:** hard · **Time:** 45 min · **Category:** Flow · operators · **Type:** implement

## Goal
Build a drug search that does not call the API on every key press, and turn a callback sensor API into a `Flow`.

A search field: the user types `i`, `ib`, `ibu`, `ibup` quickly. Calling the API on every key press would send 4 requests, and the answers could arrive in the wrong order. A good search waits until the user stops typing, skips queries that are too short or the same as the last one, and shows only the answer to the newest query.

The screen observes one `StateFlow<SearchState>`, which is `Idle`, `Loading`, `Results` or `Error`. Example: typing `ibup` within 300 ms makes one API call, for `ibup`, and the state goes `Idle` → `Loading` → `Results`.

The second part takes a heart rate sensor that reports values to a listener and turns it into a `Flow`.

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
- `stateIn(scope, SharingStarted.WhileSubscribed(5_000), initialValue)` turns a cold flow into a hot `StateFlow`; the 5 s keep it alive during a rotation.
- `callbackFlow` needs `awaitClose { unregister(listener) }`, otherwise the flow ends at once or the listener leaks.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
// debounce needs @OptIn(FlowPreview::class), flatMapLatest needs @OptIn(ExperimentalCoroutinesApi::class)
val query = MutableStateFlow("")

query.debounce(300)                              // emits after 300 ms without a new value
query.map { it.trim() }
flowOf("a", "ab", "ab").distinctUntilChanged()   // a, ab

ids.flatMapLatest { id -> flow { emit(load(id)) } }   // a new id cancels the old load

flow { emit(api.call()) }
    .onStart { emit(fallback) }                  // emitted first
    .catch { e -> emit(fallback) }               // catches errors from the operators above it

val state: StateFlow<String> = query.stateIn(scope, SharingStarted.WhileSubscribed(5_000), "")

fun values(): Flow<Int> = callbackFlow {
    val listener = Listener { value -> trySend(value) }
    source.register(listener)
    awaitClose { source.unregister(listener) }   // runs when the collector stops
}
```

- [Kotlin: asynchronous Flow](https://kotlinlang.org/docs/coroutines-flow.html)
- [Android: StateFlow and SharedFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [API: callbackFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/callback-flow.html)
- [Android: testing flows](https://developer.android.com/kotlin/flow/test)
