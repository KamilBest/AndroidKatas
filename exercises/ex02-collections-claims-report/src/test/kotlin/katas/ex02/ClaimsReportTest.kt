package katas.ex02

import katas.ex02.Category.*
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClaimsReportTest {

    private var seq = 0
    private fun claim(
        provider: String = "Medicover", category: Category = CONSULTATION, amount: Long = 100_00,
        date: String = "2026-01-15", patient: String = "p1", id: String = "c${seq++}",
    ) = Claim(id, patient, provider, category, amount, LocalDate.parse(date))

    @Test fun `total by category`() {
        val claims = listOf(claim(category = DENTAL, amount = 300), claim(category = DENTAL, amount = 200), claim(category = PHARMACY, amount = 50))
        assertEquals(mapOf(DENTAL to 500L, PHARMACY to 50L), ClaimsReport.totalByCategory(claims))
        assertEquals(emptyMap(), ClaimsReport.totalByCategory(emptyList()))
    }

    @Test fun `top providers sorted by sum then name`() {
        val claims = listOf(
            claim("Lux", amount = 100), claim("Enel", amount = 300), claim("Lux", amount = 200),
            claim("Alfa", amount = 300), claim("Health", amount = 10),
        )
        assertEquals(listOf("Alfa" to 300L, "Enel" to 300L, "Lux" to 300L), ClaimsReport.topProviders(claims, 3))
        assertEquals(4, ClaimsReport.topProviders(claims, 10).size)
    }

    @Test fun `suspected duplicates`() {
        val a1 = claim(patient = "p1", amount = 99)
        val b = claim(patient = "p2", amount = 99)
        val a2 = claim(patient = "p1", amount = 99)
        val c = claim(patient = "p1", amount = 99, date = "2026-01-16")
        val a3 = claim(patient = "p1", amount = 99)
        assertEquals(listOf(listOf(a1, a2, a3)), ClaimsReport.suspectedDuplicates(listOf(a1, b, a2, c, a3)))
    }

    @Test fun `first large claims is lazy`() {
        var produced = 0
        val infinite = generateSequence(0) { it + 1 }.map { i ->
            produced++
            claim(amount = if (i % 4 == 0) 10_000 else 1, id = "s$i")
        }
        val result = ClaimsReport.firstLargeClaims(infinite, thresholdMinor = 5_000, n = 3)
        assertEquals(listOf("s0", "s4", "s8"), result.map { it.id })
        assertTrue(produced <= 9, "processed $produced elements, expected at most 9")
    }
}
