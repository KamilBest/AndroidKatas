# ex06 · Cancellation: callback to suspend, cooperative cancellation, `NonCancellable`

**Difficulty:** hard · **Time:** 45 min · **Category:** Coroutines · cancellation · **Type:** implement

## Goal
Write code that reacts correctly when its coroutine is cancelled: it removes the listener, stops the loop and cleans up.

On Android coroutines are cancelled all the time: the user leaves the screen, a ViewModel is cleared, a newer search replaces the old one. Cancelling only *asks* a coroutine to stop. The code has to notice it and clean up, otherwise a GPS listener keeps running, a loop keeps using the CPU, or a lock is never released.

The file has four small classes, each with one of these cases: a callback API (location), a long CPU loop, cleanup that must always run, and a retry loop that by mistake ignores cancellation. You make each one stop and clean up correctly.

- **File:** `Cancellation.kt`
- **Run:** `./gradlew :ex06-coroutines-cancellation:test`

## Task
1. `LocationClient.awaitLocation()`: turn the callback API into a `suspend` function. On cancellation call `handle.cancel()`. An error from the callback becomes an exception.
2. `BatchProcessor.process(items)`: the CPU loop must stop after cancellation.
3. `Uploader.upload(file)`: always call `releaseLock()` (a `suspend` function), also after cancellation.
4. `StatusPoller.pollUntilDone()`: ignore `IOException` and retry every `intervalMs`, but stop after cancellation.

**Extra (optional):** write a test that shows the bug in the original `pollUntilDone` (from the comment in the file).

## Background
- Cancellation is cooperative: it only works at suspension points or when you check for it (`ensureActive()`, `yield()`).
- `suspendCancellableCoroutine` + `resume`/`resumeWithException` + `invokeOnCancellation` is how you wrap a callback API.
- In a cancelled coroutine every `suspend` call throws at once. `withContext(NonCancellable)` lets cleanup finish.
- `catch (e: Exception)` also catches `CancellationException`. Rethrow it or catch a narrower type.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
suspend fun awaitValue(): String = suspendCancellableCoroutine { cont ->
    val call = client.start(object : Callback {
        override fun onSuccess(value: String) = cont.resume(value)
        override fun onError(error: Throwable) = cont.resumeWithException(error)
    })
    cont.invokeOnCancellation { call.cancel() }
}

suspend fun crunch(items: List<Int>) {
    for (item in items) {
        currentCoroutineContext().ensureActive()   // throws if cancelled
        heavyStep(item)
    }
}

try {
    work()
} finally {
    withContext(NonCancellable) { closeSuspending() }  // a suspend call still runs here
}

try {
    work()
} catch (e: CancellationException) {
    throw e
} catch (e: IOException) {
    // handle
}
```

- [Kotlin: cancellation and timeouts](https://kotlinlang.org/docs/coroutines-cancellation.html)
- [API: suspendCancellableCoroutine](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/suspend-cancellable-coroutine.html)
- [Kotlin: exception handling](https://kotlinlang.org/docs/exception-handling.html)
