# ex12 · Clean Architecture: use case and dependency direction

**Difficulty:** medium · **Time:** 40 min · **Category:** Architecture · Clean Architecture · **Type:** implement + refactor

## Goal
Write the use case that refunds an invoice (dentist, physiotherapy) within a yearly limit, and keep the domain layer free of Retrofit, Room and Android. The ready `ArchitectureTest` reads imports and checks these rules.

An insured person sends an invoice (dentist, physiotherapy) and wants the money back. The policy has a yearly limit for each benefit, and a refund is paid only up to what is left of that limit.

Example: limit 1000 zł, already used 800 zł this year, invoice 300 zł gives `Accepted(200 zł)`.

The code is split into layers: `domain` (business rules) and `data` (API models). The domain must not depend on the data layer; a ready test reads the imports and checks it. You fix one wrong import, write the use case with its checks, and write the mapper from the API model.

- **Files:** `domain/UseCases.kt`, `domain/BenefitNames.kt`, `data/InvoiceDto.kt`
- **Run:** `./gradlew :ex12-clean-architecture:test`
- **Small steps:** `StepsTest.kt` tests single building blocks, start there: `./gradlew :ex12-clean-architecture:test --tests '*StepsTest'`

## Task
1. `ArchitectureTest` is red. Fix the violation: the domain imports something from `data`.
2. `SubmitReimbursementUseCase`, checking in this order:
   - invoice from the future → `InvoiceInFuture`,
   - older than 90 days → `InvoiceTooOld` (exactly 90 days is OK),
   - the benefit is not on the policy → `NotCovered`,
   - this year's limit is used up → `LimitExhausted`,
   - otherwise `Accepted(min(amount, remaining))` and `usage.record(...)`.
   Compute "today" from `Clock` in the `Europe/Warsaw` time zone.
3. `InvoiceDto.toDomain()` in the `data` layer.

**Extra (optional):** add `GetRemainingLimitsUseCase` (limit minus usage per benefit) with your own test.

## Background
- Layers: `domain` (business rules), `data` (API, database, DTOs), `presentation` (UI). Dependencies point inwards: `data` knows `domain`, never the other way.
- Dependency Inversion: the use case depends on interfaces defined in the domain (`PolicyRepository`, `UsageRepository`); `data` implements them.
- A use case is one action with `operator fun invoke`, so you call it like a function.
- Time zones matter: 23:30 UTC on 31 December is already the next year in Warsaw.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
class GreetUserUseCase(private val users: UserRepository) {
    operator fun invoke(id: String): String = "Hi ${users.find(id).name}"
}
val greet = GreetUserUseCase(repo)
greet("42")                                    // called like a function

// Mapper in the data layer
fun UserDto.toDomain(): User = User(id = id, name = fullName.trim())

val zone = ZoneId.of("Europe/Warsaw")
val today: LocalDate = LocalDate.now(clock.withZone(zone))
val days: Long = ChronoUnit.DAYS.between(someDate, today)
someDate.isAfter(today)
today.year                                     // 2026
minOf(a, b)
```

- [Android: domain layer](https://developer.android.com/topic/architecture/domain-layer)
- [Android: data layer](https://developer.android.com/topic/architecture/data-layer)
- [Java: Clock](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/Clock.html)
