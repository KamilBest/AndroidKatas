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
- `groupBy` builds a list per key. `groupingBy { }.fold/eachCount` aggregates without those lists.
- A composite key is a `data class` (or `Pair`/`Triple`): it gets `equals`/`hashCode` for free.
- `Sequence` is lazy: with `take(n)` it stops after n hits. A `List` would process everything first.
