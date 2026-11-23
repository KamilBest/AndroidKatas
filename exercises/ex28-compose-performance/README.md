# ex28 · Compose: fix the performance

**Difficulty:** hard · **Time:** 40 min · **Category:** Compose · performance · **Type:** refactor

## Goal
The code works, but it recomposes too often. Fix three typical mistakes. The tests count recompositions with "sensors" (`counter.hit(...)`) and stay red until you do.

- **File:** `Performance.kt`
- **Run:** `./gradlew :ex28-compose-performance:testDebugUnitTest` (Robolectric)

## Task
1. `DoseList`: scrolling by 10 items rebuilds the whole screen 10 times. It should happen at most once (when "Back to top" appears).
2. `SortedDoses`: changing the highlight sorts the list again. Sorting should run once, until the list changes.
3. `SlidingBanner`: every animation frame is a recomposition. There should be none, and the banner must still move.

Don't remove the `counter.hit(...)` lines.

**Extra (optional):** turn on *Layout Inspector → Show recomposition counts* in Android Studio on any screen of yours, and describe one thing you would fix.

## Background
- Compose has three phases: composition (what to show) → layout (where) → draw (pixels). Reading state in a phase invalidates that phase and the next ones.
- `derivedStateOf` passes a change on only when the *result* changes (scroll position changes often, "show the button" rarely).
- `remember(key) { … }` recomputes only when the key changes.
- State read inside a modifier lambda (`offset { }`, `graphicsLayer { }`, `drawBehind { }`) skips composition and is read only in layout or draw.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
val listState = rememberLazyListState()

// Recomposes only when the Boolean flips, not on every scrolled pixel
val showButton by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 0 }
}

// Sorted again only when `items` changes
val sorted = remember(items) { items.sortedBy { it.name } }

val offsetX by animateFloatAsState(targetValue = target, label = "x")
// State read inside the lambda: only layout / draw runs again, not composition
Box(Modifier.offset { IntOffset(offsetX.roundToInt(), 0) })
Box(Modifier.graphicsLayer { translationX = offsetX })
```

- [Android: Compose performance best practices](https://developer.android.com/develop/ui/compose/performance/bestpractices)
- [Android: Compose phases](https://developer.android.com/develop/ui/compose/phases)
