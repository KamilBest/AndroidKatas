# ex29 · Navigation Compose: routes, arguments and results

**Difficulty:** medium · **Time:** 40 min · **Category:** Compose · navigation · **Type:** implement

## Goal
Connect a prescription list and a details screen with Navigation Compose. Screens and routes are ready, only the navigation is missing.

Two screens: a list of prescriptions and the details of one prescription. Navigation Compose moves between them: it keeps a back stack of screens, passes arguments (the prescription id) and can send a result back to the previous screen.

Example: click `rx-2` → the details of `rx-2` → "Redeem" → back on the list, which now shows `"Redeemed: rx-2"`.

The screens and routes are ready. You write the `NavHost` that connects them.

- **File:** `AppNavHost.kt` (screens and routes in `Screens.kt`)
- **Run:** `./gradlew :ex29-navigation-compose:testDebugUnitTest` (Robolectric)

## Task
1. `NavHost` starts at `PrescriptionList`.
2. Clicking a prescription opens `PrescriptionDetail(id)`; the details screen gets `id` from the route.
3. "Back" returns to the list.
4. "Redeem" passes the `id` to the list (key `REDEEMED_KEY`) and goes back. The list shows `"Redeemed: rx-2"`.
5. A double click must not open the details twice (`launchSingleTop`).

**Extra (optional):** add a deep link `https://apteka.example/rx/{id}` to the details screen and a test that opens it.

## Background
- A type-safe route is a `@Serializable` class; its fields are the arguments. No string building like `"detail/{id}"`.
- Every back stack entry has its own `SavedStateHandle`. To return a result, write it into the handle of the **previous** entry, then `popBackStack()`.
- The screen's ViewModel gets the same `SavedStateHandle`, so route arguments are available there too (see ex30).
- Screens should not know `NavController`; they get lambdas, and navigation lives in one place.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
@Serializable data object Home
@Serializable data class Profile(val userId: String)

NavHost(navController, startDestination = Home) {
    composable<Home> {
        HomeScreen(onOpen = { id ->
            navController.navigate(Profile(id)) { launchSingleTop = true }
        })
    }
    composable<Profile> { entry ->
        val route: Profile = entry.toRoute()
        ProfileScreen(route.userId, onBack = { navController.popBackStack() })
    }
}

// Return a result to the previous screen
navController.previousBackStackEntry?.savedStateHandle?.set("result", "ok")
navController.popBackStack()

// Read it on the previous screen (inside its composable<...> { entry -> })
val result by entry.savedStateHandle.getStateFlow<String?>("result", null).collectAsState()
```

- [Android: navigation with Compose](https://developer.android.com/guide/navigation)
- [Android: type safety in Kotlin DSL and Navigation Compose](https://developer.android.com/guide/navigation/design/type-safety)
