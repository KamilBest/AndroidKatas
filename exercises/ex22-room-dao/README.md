# ex22 · Room: DAO, transaction and in-memory database tests

**Difficulty:** hard · **Time:** 45 min · **Category:** Database · Room · **Type:** implement

## Goal
Write the DAO of a "My medications" app: SQL queries and one transaction. Entities, the `Instant` converter and `AppDatabase` are ready.

Room generates the database code from a DAO: an interface or abstract class where each method has an annotation and, often, SQL. You write the DAO of a medication app: save medications, list them A to Z, list doses in a time range as a `Flow`, mark a dose as taken (only once), and replace future doses in one transaction.

The entities and the database class are ready. The tests run on an in-memory database.

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

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
@Dao
abstract class NoteDao {
    @Upsert                                           // insert or update
    abstract suspend fun upsert(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(notes: List<NoteEntity>)

    @Query("SELECT * FROM notes ORDER BY title COLLATE NOCASE")   // case-insensitive order
    abstract fun observeAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE createdAt BETWEEN :from AND :to ORDER BY createdAt ASC")
    abstract fun observeBetween(from: Instant, to: Instant): Flow<List<NoteEntity>>

    @Query("UPDATE notes SET title = :title WHERE id = :id")
    abstract suspend fun rename(id: Long, title: String): Int    // number of changed rows

    @Query("DELETE FROM notes WHERE folderId = :folderId AND createdAt > :after")
    abstract suspend fun deleteNewer(folderId: Long, after: Instant)

    @Transaction                                      // both calls in one transaction
    open suspend fun replace(folderId: Long, after: Instant, notes: List<NoteEntity>) {
        deleteNewer(folderId, after)
        insertAll(notes)
    }
}
```

- [Android: accessing data with DAOs](https://developer.android.com/training/data-storage/room/accessing-data)
- [Android: Room queries with Flow](https://developer.android.com/training/data-storage/room/async-queries)
- [Android: testing and debugging the database](https://developer.android.com/training/data-storage/room/testing-db)
