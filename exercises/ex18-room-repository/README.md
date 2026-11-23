# ex18 · Repository over Room: Flow from the database and mapping

**Difficulty:** medium · **Time:** 40 min · **Category:** Data · local database repository · **Type:** implement

## Goal
Write a repository that only has a local database (a dose diary). The UI observes a `Flow`, and the repository turns database rows into the screen model. `DoseDao` fakes Room, so tests are plain JVM.

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
