# ex09 · Write Flow tests (Turbine) and kill the mutants

**Difficulty:** medium · **Time:** 45 min · **Category:** Testing · Flow tests and mutation testing · **Type:** write tests

## Goal
Write tests for a heart rate monitor that are good enough to catch 11 hidden bugs (mutants).

- **File:** `VitalsMonitorTest.kt` (the code in `VitalsMonitor.kt` is ready, don't change it)
- **Run:** `./gradlew :ex09-flow-testing-mutants:test`, then `:mutationTest` (target 11/11)

## Task
Monitor rules (create the SUT with `vitalsMonitor()`):
- **HIGH:** 3 *consecutive* readings > 120. The next HIGH only after a reading ≤ 120.
- **LOW:** a reading < 40. The next LOW only after a reading ≥ 40.
- Readings outside 20..250 are **ignored** (they don't break a streak and don't count in the average).
- **averages:** after each valid reading, the average of the last 5 valid readings, `Math.round`.

A mutant "survived" = your tests did not notice the bug. Don't look into `src/mutants`.

**Extra (optional):** reach 11/11 with at most 10 tests, with names that describe the rule.

## Background
- Mutation testing changes one thing in the code (for example `>` to `>=`) and runs your tests. If they still pass, the tests missed it. It measures test quality, while coverage only measures which lines ran.
- Boundary values: test on the threshold and next to it (120/121, 39/40, 19/20, 250/251).
- Each "only after …" rule needs a test with two streaks.
- `toList()` is enough for finite flows; Turbine (`test { awaitItem(); awaitComplete() }`) helps with timing and flows that never end.
