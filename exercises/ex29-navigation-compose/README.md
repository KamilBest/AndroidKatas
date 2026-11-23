# ex29 · Navigation Compose: routes, arguments and results

**Difficulty:** medium · **Time:** 40 min · **Category:** Compose · navigation · **Type:** implement

## Goal
Connect a prescription list and a details screen with Navigation Compose. Screens and routes are ready, only the navigation is missing.

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
