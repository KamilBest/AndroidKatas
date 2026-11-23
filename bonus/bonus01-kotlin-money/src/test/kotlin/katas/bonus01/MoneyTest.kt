package katas.bonus01

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MoneyTest {

    @Test fun `parses amounts with 0, 1 or 2 decimals`() {
        assertEquals(1234L, Money.of("12.34").minor)
        assertEquals(-50L, Money.of("-0.5").minor)
        assertEquals(700L, Money.of("7").minor)
    }

    @Test fun `rejects invalid formats`() {
        listOf("1.234", "abc", "", "1,50", "1.").forEach { input ->
            assertFailsWith<IllegalArgumentException>("input=$input") { Money.of(input) }
        }
    }

    @Test fun `arithmetic operators`() {
        val a = Money.of("10.50")
        val b = Money.of("0.75")
        assertEquals(Money.of("11.25"), a + b)
        assertEquals(Money.of("9.75"), a - b)
        assertEquals(Money.of("31.50"), a * 3)
        assertEquals(Money.of("-10.50"), -a)
        assertTrue(b < a)
    }

    @Test fun `overflow is detected`() {
        assertFailsWith<ArithmeticException> { Money.ofMinor(Long.MAX_VALUE) + Money.ofMinor(1) }
        assertFailsWith<ArithmeticException> { Money.ofMinor(Long.MAX_VALUE) * 2 }
    }

    @Test fun `percent uses banker's rounding`() {
        assertEquals(Money.of("0.32"), Money.of("10.50").percent(BigDecimal("3"))) // 31.5 gr -> 32 (to even)
        assertEquals(Money.of("0.02"), Money.of("1.25").percent(BigDecimal("2"))) // 2.5 gr -> 2
        assertEquals(Money.of("0.04"), Money.of("1.75").percent(BigDecimal("2"))) // 3.5 gr -> 4
    }

    @Test fun `split keeps every grosz`() {
        assertEquals(listOf("33.34", "33.33", "33.33").map(Money::of), Money.of("100").split(3))
        assertEquals(listOf("0.01", "0.01", "0.00"), Money.of("0.02").split(3).map { it.toString().removeSuffix(" PLN") })
    }

    @Test fun `split sum always equals original - property style`() {
        for (minor in listOf(0L, 1L, 7L, 99L, 100L, 1001L, 123_457L, -1001L)) {
            for (parts in 1..12) {
                val result = Money.ofMinor(minor).split(parts)
                assertEquals(parts, result.size)
                assertEquals(minor, result.sumOf { it.minor }, "minor=$minor parts=$parts")
                assertTrue(result.maxOf { it.minor } - result.minOf { it.minor } <= 1)
            }
        }
    }

    @Test fun `split rejects non positive parts`() {
        assertFailsWith<IllegalArgumentException> { Money.of("1").split(0) }
    }

    @Test fun `allocate by ratios`() {
        assertEquals(listOf(Money.of("0.70"), Money.of("0.30")), Money.of("1").allocate(listOf(70, 30)))
        val parts = Money.of("0.05").allocate(listOf(1, 1, 1))
        assertEquals(5L, parts.sumOf { it.minor })
        assertEquals(Money.ZERO, Money.of("10").allocate(listOf(0, 1))[0])
        assertFailsWith<IllegalArgumentException> { Money.of("1").allocate(emptyList()) }
    }

    @Test fun `formats as PLN`() {
        assertEquals("12.34 PLN", Money.of("12.34").toString())
        assertEquals("-0.50 PLN", Money.of("-0.5").toString())
        assertEquals("0.00 PLN", Money.ZERO.toString())
        assertEquals("1000.05 PLN", Money.ofMinor(100005).toString())
    }
}
