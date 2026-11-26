# bonus01 · Money: `value class`, operators, rounding

**Difficulty:** easy · **Time:** 30–45 min · **Category:** Kotlin · domain types · **Type:** implement

## Goal
Write a `Money` type that counts money with no rounding errors and never loses a grosz.

Money is easy to get wrong in code: with `Double`, `0.1 + 0.2` is `0.30000000000000004`, and splitting 100 zł into 3 parts can lose a grosz. `Money` stores the amount as a whole number of grosze. It parses text like `"12.34"`, does arithmetic that fails loudly on overflow, computes percentages with a clear rounding rule, and splits amounts so the parts always add up to the total.

Example: 100.00 zł split into 3 gives `[33.34, 33.33, 33.33]`, which adds up to 100.00.

- **File:** `Money.kt`
- **Run:** `./gradlew :bonus01-kotlin-money:test`

## Task
The amount is stored in grosze (`Long`): 12.34 zł = `1234`.
1. `Money.of("12.34")`, `of("-0.5")`, `of("7")`: parse text. A wrong format or more than 2 digits after the dot → `IllegalArgumentException`.
2. Operators `+`, `-`, `* Int`, unary `-`, `<`/`>`. Overflow → `ArithmeticException`.
3. `percent(BigDecimal)`: a percentage of the amount, rounded `HALF_EVEN` to a grosz.
4. `split(n)`: split into `n` instalments, extra grosze go to the first ones: 100.00 / 3 → [33.34, 33.33, 33.33].
5. `allocate(ratios)`: split by ratio, for example [70, 30]; the parts always add up to the amount.
6. `toString()` → `"12.34 PLN"`, `"-0.50 PLN"`.

**Extra (optional):** make `split` and `allocate` share one function that hands out the remainder, and explain in a comment why the parts always add up to the amount.

## Background
- Never use `Double` for money (`0.1 + 0.2 != 0.3`). Keep whole grosze in a `Long`; use `BigDecimal` only for parsing and percentages.
- `Math.addExact` and friends throw on overflow instead of silently wrapping around.
- `HALF_EVEN` (banker's rounding) rounds halves to the even digit, so errors cancel out over many operations.
- Integer division drops the remainder; the remainder is exactly the "lost" grosze you must hand out.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
val amount = BigDecimal("12.34")
amount.scale()                                       // 2 digits after the dot
amount.movePointRight(2).longValueExact()            // 1234
BigDecimal.valueOf(1234).movePointLeft(2)            // 12.34
BigDecimal("10.005").setScale(2, RoundingMode.HALF_EVEN)   // 10.00
"12.3x".toBigDecimalOrNull()                         // null

Math.addExact(a, b)          // throws ArithmeticException on overflow
Math.subtractExact(a, b)
Math.multiplyExact(a, n.toLong())
Math.negateExact(a)

7 / 3                        // 2
7 % 3                        // 1
"%d.%02d".format(12, 5)      // "12.05"

// operator fun plus / minus / times / unaryMinus; implement Comparable for < and >
```

- [Java: BigDecimal](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/math/BigDecimal.html)
- [Java: RoundingMode](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/math/RoundingMode.html)
