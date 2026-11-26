# ex27 · Compose: side effects and lifecycle

**Difficulty:** hard · **Time:** 45 min · **Category:** Compose · side effects · **Type:** implement

## Goal
Write four small composables, each with a different side effect.

A composable function can run many times (recomposition). Work that must not repeat on every run, like loading data, registering a listener or starting a timer, is a *side effect*. Compose has special functions that tie such work to the life of the composable: start it when the composable appears, restart it when a key changes, stop it when the composable leaves.

You write four small composables, each with a different case: load data when an id changes, register and unregister a listener, a timer that calls the newest callback, and handling events only while the screen is visible.

- **File:** `SideEffects.kt`
- **Run:** `./gradlew :ex27-compose-side-effects:testDebugUnitTest` (Robolectric)

## Task
1. `DrugInfo(drugId, load)`: shows `"Loading…"`, then the result. A new `drugId` cancels the old load and starts a new one.
2. `SensorReadout(sensor)`: registers a listener while on screen and unregisters it when it leaves. Shows `"72 bpm"` or `"—"`.
3. `AutoLogout(timeoutMs, onTimeout)`: after the timeout calls the **newest** lambda. A new lambda does not restart the countdown.
4. `EventsCollector(events, onEvent)`: handles events only when the screen is at least `STARTED`. An event from the background waits and arrives after coming back.

**Extra (optional):** rewrite `DrugInfo` with `produceState`, and say how it differs from `LaunchedEffect` + `remember`.

## Background
- `LaunchedEffect(key)` runs a coroutine; a new key cancels the old one and starts again.
- `DisposableEffect(key)` is a "register / clean up" pair (`onDispose`), like `onStart`/`onStop`.
- `rememberUpdatedState(value)` lets a long-running effect see the newest value without restarting.
- `repeatOnLifecycle(STARTED)` stops collecting in the background and starts again when the screen is visible.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
// A new id cancels the old coroutine and starts a new one
var price by remember { mutableStateOf<Int?>(null) }
LaunchedEffect(id) {
    price = null
    price = load(id)
}

// Register / clean up
DisposableEffect(source) {
    val listener = Listener { value -> latest = value }
    source.add(listener)
    onDispose { source.remove(listener) }
}

// Always call the newest lambda, without restarting the effect
val currentOnDone by rememberUpdatedState(onDone)
LaunchedEffect(Unit) {
    delay(1_000)
    currentOnDone()
}

// Collect only while the screen is at least STARTED
val lifecycleOwner = LocalLifecycleOwner.current
LaunchedEffect(flow, lifecycleOwner) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        flow.collect { handle(it) }
    }
}
```

- [Android: side-effects in Compose](https://developer.android.com/develop/ui/compose/side-effects)
- [Android: lifecycle-aware coroutines](https://developer.android.com/topic/libraries/architecture/coroutines)
