# bonus02 · PESEL validator + parameterized tests

**Difficulty:** easy · **Time:** 45 min · **Category:** Testing · parameterized tests · **Type:** implement + write tests

## Goal
Write a PESEL (Polish national ID) validator that returns a result instead of throwing, and write your own parameterized test.

PESEL is the Polish national ID number: 11 digits that hold the birth date, a serial number, the sex and a check digit. A validator reads it and either returns the date and sex, or says exactly what is wrong.

Example: `44051401458` is valid: born 14 May 1944, male. `4405140145` is `WRONG_LENGTH`.

You write the validator, and one parameterized test: one test method that runs on a list of inputs.

- **Files:** `Pesel.kt`, `PeselInvalidDateTest.kt` (you write this test)
- **Run:** `./gradlew :bonus02-pesel-parametrized-tests:test`

## Task
PESEL rules (`YYMMDDZZZZC`, 11 digits):
- the month encodes the century: 1800–1899 → MM+80, 1900–1999 → MM, 2000–2099 → MM+20, 2100–2199 → MM+40, 2200–2299 → MM+60,
- checksum: weights `1 3 7 9 1 3 7 9 1 3` for digits 1–10, `C = (10 - sum % 10) % 10` = digit 11,
- an odd 10th digit = male, even = female.

1. `Pesel.parse(input)` → `Valid(date, sex)` or `Invalid(reason)`. The first error in this order: `WRONG_LENGTH` → `NOT_DIGITS` → `INVALID_DATE` → `WRONG_CHECKSUM`.
2. A parameterized test for bad dates: 31 April, 29 February 1900, month 00, day 00, month 93.

**Extra (optional):** use a table (for example a map of offsets) instead of `if`s for centuries, and test a PESEL from year 2200+.

## Background
- A `sealed` result (`Valid`/`Invalid`) makes the caller handle both cases; exceptions are for unexpected problems, not for invalid input.
- `LocalDate.of` already knows leap years and month lengths; it throws `DateTimeException` for a date that doesn't exist.
- JUnit4 `@RunWith(Parameterized::class)`: parameters come through the test class constructor; `name = "{index}: {0}"` makes each case readable. Android instrumented tests still use JUnit4.

## Reference
Syntax you will likely need. The examples use other data than the task, so they show the API, not the solution.

```kotlin
@RunWith(Parameterized::class)
class EvenTest(private val input: Int, private val expected: Boolean) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data() = listOf(
            arrayOf<Any>(2, true),
            arrayOf<Any>(3, false),
        )
    }

    @Test
    fun `checks parity`() {
        assertEquals(expected, input % 2 == 0)
    }
}

runCatching { LocalDate.of(2026, 4, 31) }.isFailure    // true: 31 April does not exist
"12345".all { it.isDigit() }                           // true
'7'.digitToInt()                                       // 7
listOf(1, 3).zip(listOf(5, 5)) { a, b -> a * b }.sum() // 20
```

- [JUnit 4: parameterized tests](https://github.com/junit-team/junit4/wiki/Parameterized-tests)
- [Java: LocalDate](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/LocalDate.html)
