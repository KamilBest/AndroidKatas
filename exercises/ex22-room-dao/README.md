# ex22 · Room: DAO, transaction and in-memory database tests

**Difficulty:** hard · **Time:** 45 min · **Category:** Database · Room · **Type:** implement

## Goal
Write the DAO of a "My medications" app: SQL queries and one transaction. Entities, the `Instant` converter and `AppDatabase` are ready.

- **File:** `MedicationDao.kt`
- **Run:** `./gradlew :ex22-room-dao:testDebugUnitTest` (Robolectric, no emulator)

## Task
Replace the `open … = TODO()` methods with abstract ones with Room annotations:
1. `upsertMedication`: insert or update.
2. `observeMedications()`: alphabetical, case-insensitive.
3. `insertDoses(list)`.
4. `observeDosesBetween(from, to)`: doses in `[from, to]`, ascending by time.
5. `markTaken(doseId, at)`: sets `takenAt` **only** when the dose was not taken yet; returns the number of changed rows.
6. `replaceFutureDoses(…)`: keeps its body, but as a `@Transaction` it deletes the medication's future untaken doses and inserts the new ones.

Room checks SQL at compile time, so a typo in a column name is a build error.

**Extra (optional):** add a `@Relation` (a medication with its list of doses) and a test for cascade delete (FK `onDelete = CASCADE`).

## Background
- A `Flow` from a DAO emits again after every change of the tables it reads (Room's `InvalidationTracker`).
- A condition in `UPDATE` (`WHERE takenAt IS NULL`) is atomic. "Read, check, write" in Kotlin has a race.
- `@Transaction` on an `open` method with a body runs several queries as one transaction.
- Use `:param` bind parameters, never string concatenation (SQL injection).
