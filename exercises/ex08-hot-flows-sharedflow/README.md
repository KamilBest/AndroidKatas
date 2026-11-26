# ex08 · Hot flows: `StateFlow` vs `SharedFlow` vs `Channel`, `shareIn`

**Difficulty:** medium · **Time:** 45 min · **Category:** Flow · hot streams · **Type:** implement

## Goal
Pick the right hot stream for three cases: an event bus, a work queue and a shared sensor.

A hot stream emits values whether anyone collects them or not. Kotlin has three common ones, and each behaves differently: `StateFlow` always holds one current value, `SharedFlow` sends each event to everyone who is listening right now, and `Channel` is a queue where each item is taken by exactly one receiver.

You get three real cases from an app and pick and configure the right one for each: session events (like "logged out") that every open screen must see, a download queue where two workers must never take the same file, and one heart rate sensor shared by several screens.

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
- `shareIn(scope, SharingStarted.WhileSubscribed(stopMs), replay)` counts subscribers and starts/stops the upstream. Call it once, in a field, not in a function.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
val state = MutableStateFlow(0)          // keeps only the latest value

val events = MutableSharedFlow<String>(
    replay = 0,                          // how many old values a new subscriber gets
    extraBufferCapacity = 8,
    onBufferOverflow = BufferOverflow.SUSPEND,   // or DROP_OLDEST, DROP_LATEST
)
events.tryEmit("hello")                  // not suspend; false when it cannot buffer
val readOnly: SharedFlow<String> = events.asSharedFlow()

val jobs = Channel<String>(Channel.UNLIMITED)   // each element goes to one receiver
jobs.trySend("job-1")
val jobFlow: Flow<String> = jobs.receiveAsFlow()

// Create it once, in a property, not in a function
val shared: SharedFlow<Int> = upstream.shareIn(
    scope,
    SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
    replay = 0,
)
```

- [Android: StateFlow and SharedFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [API: MutableSharedFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-mutable-shared-flow.html)
- [Kotlin: channels](https://kotlinlang.org/docs/channels.html)
