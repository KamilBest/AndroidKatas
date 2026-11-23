# ex05 · Coroutines: parallel loading, errors, timeout

**Difficulty:** medium · **Time:** 40 min · **Category:** Coroutines · structured concurrency · **Type:** implement

## Goal
Load three things in parallel for a "My insurance" screen, and decide what happens when one of them fails or is too slow.

- **File:** `Loading.kt`
- **Run:** `./gradlew :ex05-coroutines-basics:test`

## Task
1. `loadDashboard()`: profile, policies and claims **in parallel**. If one fails, the whole call throws and cancels the rest.
2. `loadPolicies(ids)`: all in parallel, but a failure of one policy does **not** cancel the others. Result: `Map<String, Result<Policy>>`.
3. `policyOrCached(id, timeoutMs)`: if the API is too slow, return the cached value (or `null`).

In the tests `delay(1000)` takes 0 ms and `currentTime` shows virtual time. This is how a test checks that work ran in parallel.

**Extra (optional):** add `retryWithBackoff(times, initialDelay)` with a virtual-time test. It must never retry a `CancellationException`.

## Background
- `coroutineScope`: a failing child cancels its siblings and the error goes to the caller.
- `supervisorScope`: children are independent; a failure stays inside its `Deferred`.
- `runCatching { suspendFun() }` also swallows `CancellationException`, which breaks cancellation. Rethrow it.
- `withTimeoutOrNull` returns `null` instead of throwing `TimeoutCancellationException`.
