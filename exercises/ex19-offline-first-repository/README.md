# ex19 · Offline-first repository

**Difficulty:** hard · **Time:** 50 min · **Category:** Data · offline-first · **Type:** implement

## Goal
Write a repository where the UI **always** reads from the database and the network only updates it. `ClaimsDao` and `ClaimsApi` fake Room and Retrofit, so tests are fast and plain JVM.

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
