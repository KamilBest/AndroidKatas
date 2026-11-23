# ex17 · Repository over an API: mapping, errors, cache

**Difficulty:** medium · **Time:** 40 min · **Category:** Data · network repository · **Type:** implement

## Goal
Write a repository that only talks to the network (no database): it maps DTOs to the domain, turns exceptions into domain errors, caches results and never sends the same request three times at once. `PharmacyApi` fakes Retrofit, so tests are plain JVM.

- **File:** `PharmacyRepository.kt` (types in `Contracts.kt`)
- **Run:** `./gradlew :ex17-api-repository:test`
- **Small steps:** `StepsTest.kt` tests single building blocks, start there: `./gradlew :ex17-api-repository:test --tests '*StepsTest'`

## Task
1. `PharmacyDto.toDomain()`: `"08:00-20:00"` → `opensAt`, `closesAt`. Missing hours or a strange format → both `null`.
2. `pharmacy(id)` returns an `Outcome`:
   - success → `Success(Pharmacy)`,
   - `IOException` → `Offline`, `HttpError(404)` → `NotFound`, other `HttpError` → `Server`.
3. Keep a successful result in memory for `ttl` (measured with `Clock`). Don't cache errors.
4. Several calls for the same `id` at the same time make **one** API request.

**Extra (optional):** add `invalidate(id)` and describe how this cache differs from the HTTP cache in OkHttp.

## Background
- DTO = the shape of the API, domain model = the shape your app needs. A mapper is a pure function between them.
- Upper layers should see domain errors (`Offline`, `NotFound`), not HTTP exceptions.
- Request deduplication: keep the running request (`CompletableDeferred` or `async`) in a map; later callers `await()` it.
- Guard the maps with a `Mutex`, but make the network call **outside** the lock.
