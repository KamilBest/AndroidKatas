# ex06 · Cancellation: callback to suspend, cooperative cancellation, `NonCancellable`

**Difficulty:** hard · **Time:** 45 min · **Category:** Coroutines · cancellation · **Type:** implement

## Goal
Write code that reacts correctly when its coroutine is cancelled: it removes the listener, stops the loop and cleans up.

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
