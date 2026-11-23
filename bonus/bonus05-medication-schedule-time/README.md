# bonus05 · Medication schedule: time zones and DST

**Difficulty:** hard · **Time:** 60–90 min · **Category:** Kotlin · time and time zones · **Type:** implement

## Goal
Write a medication schedule that works correctly also on the days when the clocks change (DST).

- **File:** `Schedule.kt`
- **Run:** `./gradlew :bonus05-medication-schedule-time:test`

## Task
"At 8:00 and 20:00" is wall-clock time (always 8:00 on the clock); "every 8 hours" is real elapsed time (exactly 8 h).
1. `dosesBetween(plan, from, to)`: doses (`Instant`, ascending) on days `from..to` in the patient's zone:
   - `TimesOfDay`: at the given times; a time in the **gap** (02:30 on the day clocks go forward) → 03:30; in the **overlap** (02:30 twice when clocks go back) → once, the earlier one,
   - `EveryNHours`: from `firstDose` on `startDate`, every `n` h,
   - `Weekly`: on the chosen weekdays at the given time,
   - nothing before `startDate` and after `endDate`.
2. `nextDose(plan, now)`: the first dose after `now` (search at most 8 days), or `null`.
3. `missedDoses(plan, taken, now, grace)`: today's (local) doses whose time + `grace` < `now` and that are not in `taken`.

Test dates: 2026-03-29 (02:00 → 03:00), 2026-10-25 (03:00 → 02:00), `Europe/Warsaw`.

**Extra (optional):** add tests for `America/New_York` (different DST dates) and check that nothing is hard-coded for Poland.

## Background
- `Instant` = a point in time. `LocalDate`/`LocalTime` = what is written on paper. `ZonedDateTime` joins them through a `ZoneId`.
- `ZonedDateTime.of(date, time, zone)` moves a time in the gap forward and picks the earlier offset in the overlap.
- "Every N hours": add `Duration.ofHours(n)` to an `Instant`. "At 8:00": a separate `ZonedDateTime` for each day.
- Never move to the next day with `+ 24 h`; use `plusDays` on a `LocalDate`. DST days have 23 or 25 hours.
