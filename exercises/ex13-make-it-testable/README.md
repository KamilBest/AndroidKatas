# ex13 · Refactor to testable code

**Difficulty:** medium · **Time:** 45 min · **Category:** Testability · refactoring · **Type:** refactor

## Goal
Turn an untestable booking service into a class with injected dependencies, so the ready tests pass.

`BookingService` books a doctor's visit and works in production, but you can't test it. It reads the real clock, makes random ids, calls a global HTTP singleton and starts background work in `GlobalScope`. A test can't say "it is Monday 10:00", can't know the id in advance, and can't replace the network.

You refactor it into a class that gets all of these through its constructor, without changing what it does. The ready tests pass fakes (a fixed clock, fixed ids, a fake API) and stay red until the refactor is done.

- **File:** `BookingService.kt` (tests are ready; they don't compile before the refactor)
- **Run:** `./gradlew :ex13-make-it-testable:test detektMain`

## Task
1. At the top of the file list the hidden dependencies (`now()`, `UUID`, the HTTP singleton, `GlobalScope`, `println`) and what each one breaks in tests.
2. `object BookingService` → class `BookingService(clock, ids, api, analytics, appScope)`.
3. Same logic (see KDoc): less than 2 h from now → `TooLate`; weekend → `ClinicClosed`; `IOException` → `Failed(true)`, other error → `Failed(false)`; success → a "booking_created" event in the background and `Booked(id, "WAW-" + first 6 characters of id in upper case)`. Time is in the `Europe/Warsaw` zone.
4. Detekt forbids `now()` without a clock and `GlobalScope`.

**Extra (optional):** add your own test for the 2 h limit across a DST change (for example 29 March 2026).

## Background
- A hidden dependency is anything a test cannot replace: the current time, random ids, singletons, global scopes.
- Constructor injection makes them visible and replaceable. `Clock.fixed(...)` in tests, `LocalDateTime.now(clock.withZone(zone))` in code.
- A "seam" is a place where you can change behaviour without editing the code, for example an interface passed in the constructor.
- Fire-and-forget work belongs to an injected application `CoroutineScope`, not `GlobalScope`, so tests can wait for it.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
fun interface IdGenerator { fun newId(): String }
interface Analytics { fun track(name: String) }

class Greeter(
    private val clock: Clock,
    private val ids: IdGenerator,
    private val analytics: Analytics,
    private val appScope: CoroutineScope,      // instead of GlobalScope
) {
    fun greet(): String {
        val now = LocalDateTime.now(clock.withZone(ZoneId.of("Europe/Warsaw")))
        appScope.launch { analytics.track("greeted") }   // fire-and-forget
        return "${ids.newId()} at $now"
    }
}

Duration.between(start, end) < Duration.ofHours(2)
date.dayOfWeek in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
"abcdef1234".take(6).uppercase()               // ABCDEF

// In a test
val clock = Clock.fixed(Instant.parse("2026-03-02T10:00:00Z"), ZoneOffset.UTC)
val greeter = Greeter(clock, ids = { "id-1" }, analytics = FakeAnalytics(), appScope = backgroundScope)
```

- [Android: coroutines best practices (inject dispatchers and scopes)](https://developer.android.com/kotlin/coroutines/coroutines-best-practices)
- [Android: test doubles](https://developer.android.com/training/testing/fundamentals/test-doubles)
- [Java: Clock](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/Clock.html)
