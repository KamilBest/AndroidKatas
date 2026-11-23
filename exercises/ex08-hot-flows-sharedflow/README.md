# ex08 · Hot flows: `StateFlow` vs `SharedFlow` vs `Channel`, `shareIn`

**Difficulty:** medium · **Time:** 45 min · **Category:** Flow · hot streams · **Type:** implement

## Goal
Pick the right hot stream for three cases: an event bus, a work queue and a shared sensor.

- **File:** `HotFlows.kt`
- **Run:** `./gradlew :ex08-hot-flows-sharedflow:test`

## Task
1. **`SessionEvents`:** each event goes to all active subscribers; a new subscriber does not get old events; `publish` is not `suspend`, has a buffer of 16, and drops the oldest event on overflow.
2. **`DownloadQueue`:** each job goes to exactly one worker.
3. **`HeartRateHub`:** one upstream for many screens; it starts with the first subscriber and stops 2 s after the last one; a new subscriber gets the latest reading at once.
4. **`latestOnlyDemo`** (in the test): fill in what a slow `StateFlow` collector sees when values are emitted fast.

**Extra (optional):** write a test that shows `shareIn` called inside a function creates two upstreams.

## Background
- State → `StateFlow` (keeps only the latest value, conflates). Events for many listeners → `SharedFlow`. Work queue → `Channel` (each element goes to one receiver).
- `MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow)`: `replay` is history for new subscribers, the buffer lets `tryEmit` succeed without waiting.
- `shareIn(scope, WhileSubscribed(stop), replay)` counts subscribers and starts/stops the upstream. Call it once, in a field, not in a function.
