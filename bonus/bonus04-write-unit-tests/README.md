# bonus04 · Write unit tests for a premium calculator (mutation testing)

**Difficulty:** medium · **Time:** 60 min · **Category:** Testing · unit tests and mutation testing · **Type:** write tests

## Goal
Write tests for an insurance premium calculator that catch 13 hidden bugs (mutants).

- **File:** `PremiumCalculatorTest.kt` (the calculator is ready, don't change it)
- **Run:** `./gradlew :bonus04-write-unit-tests:mutationTest` (target 13/13)

## Task
| Rule | Value |
|---|---|
| Base price / month | BASIC 50 zł, STANDARD 90 zł, PREMIUM 150 zł |
| Age | < 18 → exception · 18–25 ×0.8 · 26–45 ×1.0 · 46–60 ×1.4 · 61+ ×2.0 |
| Smoker | ×1.25 (holder's premium) |
| Family member | +40% of the base price per person, max 4 people; a negative number → exception |
| Loyalty | 3–4 years −5%, 5+ years −10% (of the total) |
| Yearly | 12 × monthly × 0.95 |
| Rounding | to a grosz, `HALF_UP`, only at the end |

SUT: `premiumCalculator()`, result in grosze.

**Extra (optional):** at most 12 tests, with the boundaries as a parameterized test.

## Background
- Equivalence classes: one representative test per group of inputs that behave the same.
- Boundary values: test both sides of each boundary (17/18, 25/26…), because `<` vs `<=` bugs only show there.
- Compute the expected result by hand and write the calculation in a comment. Copying it from the code tests nothing.
- Mutation testing (see ex09) shows which bugs your tests would miss.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
@Test
fun `buyer under 18 is rejected`() {
    assertFailsWith<IllegalArgumentException> { ticketPrice(age = 17) }
}

@Test
fun `18 year old pays 80 percent`() {
    // 50.00 zł * 0.8 = 40.00 zł = 4000 grosze
    assertEquals(4000, ticketPrice(age = 18))
}
```

- [kotlin.test API](https://kotlinlang.org/api/core/kotlin-test/)
- [Java: RoundingMode](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/math/RoundingMode.html)
