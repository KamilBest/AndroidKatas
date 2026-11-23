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

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
suspend fun loadBoth(): Pair<User, Feed> = coroutineScope {
    val user = async { api.user() }      // starts now
    val feed = async { api.feed() }      // runs in parallel with user
    user.await() to feed.await()         // one fails -> the other is cancelled
}

val all: List<Item> = ids.map { id -> async { api.item(id) } }.awaitAll()

supervisorScope {
    val a = async { api.item("a") }
    val b = async { api.item("b") }
    val first = try { a.await() } catch (e: IOException) { null }  // b keeps running
}

val item: Item? = withTimeoutOrNull(500) { api.item("1") }  // null after 500 ms

try {
    api.item("1")
} catch (e: CancellationException) {
    throw e                               // never swallow cancellation
} catch (e: IOException) {
    null
}
```

- [Kotlin: composing suspending functions (async)](https://kotlinlang.org/docs/composing-suspending-functions.html)
- [Kotlin: exceptions and supervision](https://kotlinlang.org/docs/exception-handling.html)
- [Kotlin: cancellation and timeouts](https://kotlinlang.org/docs/coroutines-cancellation.html)
- [Android: testing coroutines (runTest, virtual time)](https://developer.android.com/kotlin/coroutines/test)
