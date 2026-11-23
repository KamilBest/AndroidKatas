# ex16 · Hilt: modules, qualifiers, scopes and test replacement

**Difficulty:** medium · **Time:** 45 min · **Category:** DI · Hilt · **Type:** implement

## Goal
The app classes are ready, only the Hilt wiring is missing. Add it, so that the graph builds and a test can replace a dependency.

- **Files:** `Modules.kt` (modules) and `Prescriptions.kt` (annotations only, no logic changes)
- **Run:** `./gradlew :ex16-hilt-di:testDebugUnitTest` (Robolectric + `HiltTestApplication`)

## Task
1. A qualifier `@IoDispatcher` and a `DispatchersModule` that provides `@IoDispatcher CoroutineDispatcher` = `Dispatchers.IO`.
2. `ApiModule` (object): provides `PrescriptionApi` as `InMemoryPrescriptionApi`, singleton. The test replaces this module with a fake.
3. `DataModule` (abstract class): binds `PrescriptionsRepository` to `DefaultPrescriptionsRepository`, singleton.
4. Annotations in `Prescriptions.kt`: `@Inject constructor` where needed, `HeaderFormatter` without a scope, `@HiltViewModel` on the ViewModel.

The test does not compile until `ApiModule` and `@IoDispatcher` exist. That is on purpose: the test describes what it expects from the graph.

**Extra (optional):** add `@MainDispatcher`, and describe in a comment what happens when a singleton gets an `@ActivityScoped` dependency (captive dependency).

## Background
- `@Binds` maps "interface → my class with `@Inject`". `@Provides` builds objects you don't construct yourself (Retrofit, `Dispatchers.IO`).
- A qualifier tells apart two dependencies of the same type.
- No scope annotation = a new instance for every injection. A scope (`@Singleton`) ties one instance to a component's lifetime.
- `@TestInstallIn(replaces = [...])` replaces a module for the whole test set; `@UninstallModules` for one test.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {
    @Provides
    @DefaultDispatcher
    fun provideDefault(): CoroutineDispatcher = Dispatchers.Default
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUsers(impl: DefaultUserRepository): UserRepository
}

class DefaultUserRepository @Inject constructor(
    private val api: UserApi,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : UserRepository

@HiltViewModel
class UserViewModel @Inject constructor(private val users: UserRepository) : ViewModel()

// Test: replace a module for all tests
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [CoroutinesModule::class])
object TestCoroutinesModule {
    @Provides
    @DefaultDispatcher
    fun provideDefault(): CoroutineDispatcher = UnconfinedTestDispatcher()
}
```

- [Android: dependency injection with Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Android: Hilt testing guide](https://developer.android.com/training/dependency-injection/hilt-testing)
