# ex19 · Offline-first repository

**Difficulty:** hard · **Time:** 50 min · **Category:** Data · offline-first · **Type:** implement

## Goal
Write a repository where the UI **always** reads from the database and the network only updates it. `ClaimsDao` and `ClaimsApi` fake Room and Retrofit, so tests are fast and plain JVM.

An offline-first app works without the network: the screen always shows what is in the local database, and the network only refreshes that database. The screen never waits for the network and never goes empty when the phone is offline.

You write the repository that connects both sides: mappers between the API, database and app models, observing claims from the database, and a `refresh` that skips the network when the data is fresh, saves new data on success, keeps the old data on error, and never runs twice at the same time.

- **File:** `OfflineFirstClaimsRepository.kt` (types in `Contracts.kt`)
- **Run:** `./gradlew :ex19-offline-first-repository:test`
- **Small steps:** `StepsTest.kt` tests single building blocks, start there: `./gradlew :ex19-offline-first-repository:test --tests '*StepsTest'`

## Task
1. Mappers: `ClaimDto.toEntity()` (amount `"123.45"` → grosze) and `ClaimEntity.toDomain()` (unknown status → `UNKNOWN`).
2. `observeClaims()`: data from the DAO mapped to the domain.
3. `refresh(force = false)`:
   - the last sync is younger than `maxAge` and no `force` → `Skipped`, no network,
   - otherwise fetch, replace the data in the database, save the time → `Updated(count)`,
   - error → `Failed(Network)` for `IOException`, `Failed(Unknown)` for the rest; the database stays unchanged.
4. Three `refresh()` calls at the same time make **one** request; the others get `Skipped`.

**Extra (optional):** instead of a `Mutex`, share the running request (`Deferred`) so every waiting caller gets `Updated`, and describe the difference.

## Background
- Single source of truth: the screen observes the database. The network writes into the database, and the database `Flow` updates the screen. Offline you still see the last data.
- Three models: `Dto` (API), `Entity` (database), domain (app). Mappers are pure functions.
- `Mutex` + the freshness check *inside* the lock: whoever waited sees the new sync time and skips the network.
- An unknown status from the backend must not crash the app, so map it to `UNKNOWN`.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
BigDecimal("123.45").movePointRight(2).longValueExact()   // 12345

enum class Status { OPEN, CLOSED, UNKNOWN }
val status = Status.entries.firstOrNull { it.name == raw } ?: Status.UNKNOWN

fun observe(): Flow<List<Item>> = dao.observeAll().map { rows -> rows.map { it.toDomain() } }

val mutex = Mutex()

suspend fun sync(): SyncResult = mutex.withLock {
    if (isFresh()) SyncResult.Skipped          // checked inside the lock
    else SyncResult.Updated(download())
}

Duration.between(lastSync, clock.instant()) < maxAge
```

- [Android: offline-first apps](https://developer.android.com/topic/architecture/data-layer/offline-first)
- [Kotlin: shared mutable state (Mutex)](https://kotlinlang.org/docs/shared-mutable-state-and-concurrency.html)
- [Android: data layer](https://developer.android.com/topic/architecture/data-layer)
