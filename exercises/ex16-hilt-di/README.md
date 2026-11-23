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
