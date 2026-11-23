# ex02 · Claims report with collection operators

**Difficulty:** easy · **Time:** 35 min · **Category:** Kotlin · collections · **Type:** implement

## Goal
Build a small report from a list of claims using collection operators instead of loops.

- **File:** `ClaimsReport.kt` (each function has a KDoc)
- **Run:** `./gradlew :ex02-collections-claims-report:test`

## Task
1. `totalByCategory`: the sum of amounts in each category.
2. `topProviders`: the N providers with the biggest sum; on a tie, sort by name.
3. `suspectedDuplicates`: groups of claims with the same patient, provider, amount and date.
4. `firstLargeClaims`: the first N large claims from an infinite sequence.

Each function fits in 1–4 lines.

**Extra (optional):** rewrite `totalByCategory` so it creates no intermediate lists, and say how many passes over the data each version makes.

## Background
- Pick the operator from the shapes: List → Map is `groupBy`/`associate`, Map → List is `map`/`toList`.
- `groupBy` builds a list for each key. `groupingBy` followed by `eachCount()` or `fold(...)` counts or sums without building those lists.
- A composite key is a `data class` (or `Pair`/`Triple`): it gets `equals`/`hashCode` for free.
- `Sequence` is lazy: with `take(n)` it stops after n hits. A `List` would process everything first.
- `groupBy` keeps keys in first-appearance order (it returns a `LinkedHashMap`), so no extra sorting is needed for that.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
val words = listOf("apple", "avocado", "banana")

words.groupBy { it.first() }                    // {a=[apple, avocado], b=[banana]}
words.groupingBy { it.first() }.eachCount()     // {a=2, b=1}
words.groupingBy { it.first() }
    .fold(0) { total, word -> total + word.length }  // {a=12, b=6}

words.sumOf { it.length }                       // 18
mapOf("a" to 2, "b" to 1).toList()              // [(a, 2), (b, 1)]
mapOf("a" to 2, "b" to 1).values                // [2, 1]

// Sort by length (longest first), then alphabetically
words.sortedWith(compareByDescending<String> { it.length }.thenBy { it })  // [avocado, banana, apple]
words.take(2)                                   // [apple, avocado]

// Composite key: Pair, Triple or your own data class
words.groupBy { it.first() to it.length }

// Lazy: stops after the 3rd match
generateSequence(1) { it + 1 }.filter { it % 2 == 0 }.take(3).toList()  // [2, 4, 6]
```

- [Kotlin: collection operations overview (start here)](https://kotlinlang.org/docs/collection-operations.html)
- [Kotlin: grouping](https://kotlinlang.org/docs/collection-grouping.html)
- [Kotlin: ordering (comparators, thenBy)](https://kotlinlang.org/docs/collection-ordering.html)
- [Kotlin: aggregate operations (sumOf, fold)](https://kotlinlang.org/docs/collection-aggregate.html)
- [Kotlin: sequences](https://kotlinlang.org/docs/sequences.html)
- [API reference: kotlin.collections](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/)
