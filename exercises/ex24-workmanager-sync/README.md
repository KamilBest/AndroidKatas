# ex24 · WorkManager: background sync

**Difficulty:** medium · **Time:** 45 min · **Category:** Android · WorkManager · **Type:** implement

## Goal
A patient sent a claim with no signal, so it went into a queue (outbox). When the network is back, WorkManager must send it, even if the app is closed. Write the worker, its factory and the scheduling.

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
