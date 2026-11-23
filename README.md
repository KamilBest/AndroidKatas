# Android Katas

Short Kotlin and Android exercises to keep practising writing code by hand in the age of AI.

Each kata is a Gradle module with a task (`README.md`) and ready tests. Write code until the tests pass, e.g. `./gradlew :ex01-sealed-state-machine:test`.

The `main` branch has only the tasks and starter code, so you can fork it (keep "Copy the main branch only" checked) and solve the katas yourself. My solutions are on the [`my-solutions`](https://github.com/KamilBest/AndroidKatas/tree/my-solutions) branch and linked in the last column.

## Level 1: Kotlin

| # | Kata | Topics | My solution |
|---|---|---|---|
| ex01 | [Claim state machine](exercises/ex01-sealed-state-machine/README.md) | `sealed`, exhaustive `when`, reducer | [ClaimMachine.kt](https://github.com/KamilBest/AndroidKatas/blob/my-solutions/exercises/ex01-sealed-state-machine/src/main/kotlin/katas/ex01/ClaimMachine.kt) |
| ex02 | [Claims report with collection operators](exercises/ex02-collections-claims-report/README.md) | `groupBy`, `sortedWith`, composite key, `Sequence` |  |
| ex03 | [LRU cache in O(1)](exercises/ex03-data-structures/README.md) | `HashMap` + doubly linked list, O(1), generics |  |
| ex04 | [Kotlin idioms](exercises/ex04-kotlin-idioms/README.md) | `value class`, delegation, variance, `reified`, `equals`/`hashCode` |  |

## Level 2: Coroutines and Flow

| # | Kata | Topics | My solution |
|---|---|---|---|
| ex05 | [Parallel loading](exercises/ex05-coroutines-basics/README.md) | `async`, `supervisorScope`, `withTimeoutOrNull` |  |
| ex06 | [Cancellation](exercises/ex06-coroutines-cancellation/README.md) | `suspendCancellableCoroutine`, `ensureActive`, `NonCancellable` |  |
| ex07 | [Search with Flow](exercises/ex07-flow-operators/README.md) | `debounce`, `flatMapLatest`, `stateIn`, `callbackFlow` |  |
| ex08 | [Hot flows](exercises/ex08-hot-flows-sharedflow/README.md) | `StateFlow` vs `SharedFlow` vs `Channel`, `shareIn` |  |
| ex09 | [Flow tests + mutants](exercises/ex09-flow-testing-mutants/README.md) | Turbine, boundary tests, mutation testing |  |

## Level 3: Architecture and testability

| # | Kata | Topics | My solution |
|---|---|---|---|
| ex10 | [MVVM ViewModel](exercises/ex10-mvvm-viewmodel/README.md) | `UiState`, one-off events, `MainDispatcherRule` |  |
| ex11 | [MVI reducer](exercises/ex11-mvi-reducer/README.md) | pure reducer, effects, races |  |
| ex12 | [Clean Architecture](exercises/ex12-clean-architecture/README.md) | use case, dependency direction, architecture test |  |
| ex13 | [Make it testable](exercises/ex13-make-it-testable/README.md) | hidden dependencies, `Clock`, injected scope |  |
| ex14 | [Fakes and contract tests](exercises/ex14-fakes-contract-tests/README.md) | test doubles, one contract for many implementations |  |
| ex15 | [SavedStateHandle](exercises/ex15-savedstate-viewmodel/README.md) | process death, navigation arguments |  |
| ex16 | [Hilt](exercises/ex16-hilt-di/README.md) | `@Module`, `@Binds`/`@Provides`, qualifiers, `@TestInstallIn` |  |

## Level 4: Data: repositories, network, database

| # | Kata | Topics | My solution |
|---|---|---|---|
| ex17 | [Repository over an API](exercises/ex17-api-repository/README.md) | DTO → domain, domain errors, TTL cache, deduplication |  |
| ex18 | [Repository over Room](exercises/ex18-room-repository/README.md) | `Flow` from DAO, mapping, time zone, atomic write |  |
| ex19 | [Offline-first repository](exercises/ex19-offline-first-repository/README.md) | API + database, single source of truth, `Mutex` |  |
| ex20 | [Retrofit + MockWebServer](exercises/ex20-retrofit-mockwebserver/README.md) | annotations, serialization, HTTP codes, HTTP tests |  |
| ex21 | [OkHttp auth](exercises/ex21-okhttp-auth/README.md) | `Interceptor`, `Authenticator`, token refresh |  |
| ex22 | [Room DAO](exercises/ex22-room-dao/README.md) | queries, `@Transaction`, in-memory database tests |  |
| ex23 | [Paging 3](exercises/ex23-paging3/README.md) | `PagingSource`, page keys, `TestPager` |  |
| ex24 | [WorkManager](exercises/ex24-workmanager-sync/README.md) | `CoroutineWorker`, retry, constraints, unique work |  |

## Level 5: Compose, navigation, final tasks

| # | Kata | Topics | My solution |
|---|---|---|---|
| ex25 | [Compose: stateless screen](exercises/ex25-compose-state-ui/README.md) | state hoisting, semantics, `LazyColumn` |  |
| ex26 | [Compose UI tests](exercises/ex26-compose-write-ui-tests/README.md) | finders, actions, assertions, robot |  |
| ex27 | [Compose: side effects](exercises/ex27-compose-side-effects/README.md) | `LaunchedEffect`, `DisposableEffect`, `rememberUpdatedState`, lifecycle |  |
| ex28 | [Compose: performance](exercises/ex28-compose-performance/README.md) | `derivedStateOf`, `remember(key)`, deferred state reads |  |
| ex29 | [Navigation Compose](exercises/ex29-navigation-compose/README.md) | type-safe routes, arguments, result to the previous screen |  |
| ex30 | [Mini take-home: list and details](exercises/ex30-take-home-list-detail/README.md) | list and details ViewModels, screen states, errors |  |
| ex31 | [Code review a PR](exercises/ex31-code-review/README.md) | security/PII, leaks, coroutines, Compose, priorities |  |

## Bonus

| # | Kata | Topics | My solution |
|---|---|---|---|
| bonus01 | [Money](bonus/bonus01-kotlin-money/README.md) | `BigDecimal`, rounding, splitting money without losing a grosz |  |
| bonus02 | [PESEL](bonus/bonus02-pesel-parametrized-tests/README.md) | parameterized tests in JUnit4 |  |
| bonus03 | [Minesweeper + BFS](bonus/bonus03-grid-minesweeper/README.md) | grid algorithms and BFS |  |
| bonus04 | [Premium calculator tests](bonus/bonus04-write-unit-tests/README.md) | a second round of mutation testing after ex09 |  |
| bonus05 | [Medication schedule (DST)](bonus/bonus05-medication-schedule-time/README.md) | `java.time`, time zones and clock changes |  |

---
The task descriptions and tests were written with AI help. The solutions are my own code.
