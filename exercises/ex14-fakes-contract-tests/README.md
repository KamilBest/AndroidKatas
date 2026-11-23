# ex14 · Fakes and contract tests

**Difficulty:** medium · **Time:** 35 min · **Category:** Testing · fakes and contract tests · **Type:** implement + write tests

## Goal
Write a fake repository and run one shared test suite (a contract) on every implementation. The contract will find a bug in the "production" one.

- **Files:** `InMemoryPatientRepository.kt`, `RegisterPatientUseCase.kt`, `TablePatientRepository.kt`, new tests in `src/test`
- **Run:** `./gradlew :ex14-fakes-contract-tests:test`

## Task
1. `InMemoryPatientRepository`: a fake built on `MutableStateFlow<Map<String, Patient>>`. Save = insert or update by id, deleting a missing id does nothing, `observeAll` is sorted by `name` and emits after every change.
2. `InMemoryPatientRepositoryTest : PatientRepositoryContract()` (3 lines). The contract is ready.
3. The same for `TablePatientRepository`. The contract finds a bug in it: find it, name it in a comment and fix it.
4. `RegisterPatientUseCase`: the name after `trim` is not empty, the PESEL is unique, the id comes from `newId()`.

**Extra (optional):** add one test of your own to the contract that passes on both implementations after the fix.

## Background
- Test doubles: a **fake** is a working, simplified implementation; a **stub** returns fixed answers; a **spy** records calls; a **mock** is a library object you `verify`.
- How do you know the fake behaves like the real thing? With a **contract test**: an abstract test class with `abstract fun createRepository()`, run once per implementation.
- Fakes make tests test behaviour, not calls, so they break less when the implementation changes.
