# bonus01 · Money: `value class`, operators, rounding

**Difficulty:** easy · **Time:** 30–45 min · **Category:** Kotlin · domain types · **Type:** implement

## Goal
Write a `Money` type that counts money with no rounding errors and never loses a grosz.

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
