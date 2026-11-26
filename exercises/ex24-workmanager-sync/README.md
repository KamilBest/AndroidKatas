# ex24 · WorkManager: background sync

**Difficulty:** medium · **Time:** 45 min · **Category:** Android · WorkManager · **Type:** implement

## Goal
A patient sent a claim with no signal, so it went into a queue (outbox). When the network is back, WorkManager must send it, even if the app is closed. Write the worker, its factory and the scheduling.

Some work must finish even if the user closes the app, for example sending a claim that was written offline. WorkManager runs such work when its conditions are met (the network is available), retries it later after a failure, and survives app and phone restarts.

Unsent claims wait in an `Outbox`. You write the worker that sends them and reports success, retry or failure; a factory that gives the worker its `Outbox`; and the scheduling: only with network, with backoff, and never two syncs in the queue.

- **File:** `SyncWorker.kt` (`Outbox` is ready)
- **Run:** `./gradlew :ex24-workmanager-sync:testDebugUnitTest` (Robolectric)

## Task
1. `SyncWorker.doWork()` calls `outbox.sendPending()`:
   - nothing to retry → `success` with the number sent under the key `KEY_SENT`,
   - some items to retry → `retry()`, but on the last of `MAX_ATTEMPTS` attempts → `failure()`.
2. `SyncWorkerFactory`: creates `SyncWorker` with the injected `Outbox`, returns `null` for other classes.
3. `SyncScheduler.syncRequest()`: only with network, exponential backoff starting at 30 s.
4. `SyncScheduler.schedule()`: unique work `UNIQUE_NAME`; when it is already waiting, a second call changes nothing.

**Extra (optional):** add a periodic sync every 6 h (`PeriodicWorkRequest`, `ExistingPeriodicWorkPolicy.UPDATE`) and a test that there is only one.

## Background
- WorkManager is for deferrable work that must run even after the app or the phone restarts.
- `retry()` leaves the timing to WorkManager (backoff); you never `delay` in a worker.
- Constraints (network) and backoff belong to the request, not to the worker code.
- Unique work + `ExistingWorkPolicy.KEEP` means many screens can call `schedule()` and only one sync is queued.
- A custom `WorkerFactory` (or `HiltWorkerFactory`) is the only way to pass dependencies to a worker's constructor.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
class UploadWorker(
    context: Context,
    params: WorkerParameters,
    private val uploader: Uploader,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        runAttemptCount                                  // 0 on the first run
        return Result.success(workDataOf("count" to 3))  // or Result.retry(), Result.failure()
    }
}

class AppWorkerFactory(private val uploader: Uploader) : WorkerFactory() {
    override fun createWorker(context: Context, className: String, params: WorkerParameters): ListenableWorker? =
        if (className == UploadWorker::class.java.name) UploadWorker(context, params, uploader) else null
}

val request = OneTimeWorkRequestBuilder<UploadWorker>()
    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build())
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .build()
WorkManager.getInstance(context)
    .enqueueUniqueWork("upload", ExistingWorkPolicy.REPLACE, request)   // or KEEP, APPEND

// Test
val worker = TestListenableWorkerBuilder<UploadWorker>(context)
    .setWorkerFactory(AppWorkerFactory(fakeUploader))
    .setRunAttemptCount(2)
    .build()
val result = worker.doWork()
result.outputData.getInt("count", -1)
```

- [Android: define work requests](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work)
- [Android: managing work (unique work)](https://developer.android.com/develop/background-work/background-tasks/persistent/how-to/manage-work)
- [Android: testing workers](https://developer.android.com/develop/background-work/background-tasks/testing/persistent/worker-impl)
