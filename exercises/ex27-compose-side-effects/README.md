# ex27 · Compose: side effects and lifecycle

**Difficulty:** hard · **Time:** 45 min · **Category:** Compose · side effects · **Type:** implement

## Goal
Write four small composables, each with a different side effect.

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
