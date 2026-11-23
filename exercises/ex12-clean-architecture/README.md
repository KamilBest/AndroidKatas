# ex12 · Clean Architecture: use case and dependency direction

**Difficulty:** medium · **Time:** 40 min · **Category:** Architecture · Clean Architecture · **Type:** implement + refactor

## Goal
Write the use case that refunds an invoice (dentist, physiotherapy) within a yearly limit, and keep the domain layer free of Retrofit, Room and Android. The ready `ArchitectureTest` reads imports and checks these rules.

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
