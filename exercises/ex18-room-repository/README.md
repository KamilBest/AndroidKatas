# ex18 · Repository over Room: Flow from the database and mapping

**Difficulty:** medium · **Time:** 40 min · **Category:** Data · local database repository · **Type:** implement

## Goal
Write a repository that only has a local database (a dose diary). The UI observes a `Flow`, and the repository turns database rows into the screen model. `DoseDao` fakes Room, so tests are plain JVM.

A dose diary: the user sees today's medicine doses and marks them as taken. Everything is stored only in a local database, and the screen observes it as a `Flow`, so the list updates by itself after every change.

The repository turns database rows (times as `Instant`, a "taken at" field) into what the screen shows: the name and dose, the local time, and a status `TAKEN`, `MISSED` or `PENDING`.

Example: a dose planned for 08:00, now 09:00, `missedAfter` 30 min, not taken: `MISSED`.

- **File:** `DoseRepository.kt` (types in `Contracts.kt`)
- **Run:** `./gradlew :ex18-room-repository:test`
- **Small steps:** `StepsTest.kt` tests single building blocks, start there: `./gradlew :ex18-room-repository:test --tests '*StepsTest'`

## Task
1. `todayDoses()`: today's doses in the patient's time zone, mapped to `Dose`:
   - label `"Ibuprom 200 mg"`, local time,
   - status: taken → `TAKEN`; more than `missedAfter` since the plan → `MISSED`; otherwise `PENDING`.
2. `pendingCount()`: the number of `PENDING` doses, emitted only when the number changes.
3. `take(id)`: mark the dose as taken "now". A second try → `AlreadyTaken`.

**Extra (optional):** what happens to `todayDoses()` at midnight? Propose (and test) a version that switches to the new day by itself.

## Background
- The database is the source of truth: after `take` you don't update anything by hand, the DAO `Flow` emits the new list.
- Day boundaries are computed in the patient's zone (`atStartOfDay(zone)`), not in UTC and not with `+ 24 h` (DST days have 23 or 25 hours).
- `distinctUntilChanged` drops emissions that change nothing for the UI.
- An atomic `UPDATE … WHERE takenAt IS NULL` returns the number of changed rows: 0 means someone already took it.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
val zone = ZoneId.of("Europe/Warsaw")
val today: LocalDate = LocalDate.now(clock.withZone(zone))
val dayStart: Instant = today.atStartOfDay(zone).toInstant()
val dayEnd: Instant = today.plusDays(1).atStartOfDay(zone).toInstant()

instant.atZone(zone).toLocalTime()                    // 08:00
LocalTime.of(8, 0).format(DateTimeFormatter.ofPattern("HH:mm"))   // "08:00"
Duration.between(plannedAt, clock.instant()) > Duration.ofMinutes(30)

dao.observeBetween(dayStart, dayEnd)
    .map { rows -> rows.map { it.toUi() } }
    .distinctUntilChanged()                           // only when the value really changes

val changed: Int = dao.markDone(id, clock.instant())  // number of updated rows
if (changed == 0) { /* someone did it already */ }
```

- [Android: Room queries with Flow](https://developer.android.com/training/data-storage/room/async-queries)
- [Kotlin: asynchronous Flow](https://kotlinlang.org/docs/coroutines-flow.html)
- [Java: ZonedDateTime](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/ZonedDateTime.html)
