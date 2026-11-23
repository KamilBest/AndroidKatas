package katas.ex17

import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

// GIVEN: tests for single steps. Start with them, then PharmacyRepositoryTest.
class StepsTest {
    @Test fun `parses opening hours`() {
        assertEquals(LocalTime.of(8, 0) to LocalTime.of(20, 0), parseOpeningHours("08:00-20:00"))
        assertEquals(LocalTime.of(7, 30) to LocalTime.of(22, 0), parseOpeningHours("07:30 - 22:00"))
        assertNull(parseOpeningHours(null))
        assertNull(parseOpeningHours("all day"))
        assertNull(parseOpeningHours("8-20-22"))
    }

    @Test fun `maps dto to domain`() {
        val dto = PharmacyDto("p1", "Linden Pharmacy", "08:00-20:00", 4.6)
        assertEquals(Pharmacy("p1", "Linden Pharmacy", LocalTime.of(8, 0), LocalTime.of(20, 0), 4.6), dto.toDomain())
        assertEquals(Pharmacy("p1", "Linden Pharmacy", null, null, null), dto.copy(openingHours = null, rating = null).toDomain())
    }

    @Test fun `http errors map to domain errors`() {
        assertEquals(DataError.NotFound, HttpError(404).toDataError())
        assertEquals(DataError.Server, HttpError(503).toDataError())
        assertEquals(DataError.Server, HttpError(400).toDataError())
    }

    @Test fun `cache freshness`() {
        val t0 = Instant.parse("2026-05-01T10:00:00Z")
        val ttl = Duration.ofMinutes(5)
        assertTrue(isFresh(t0, t0.plus(Duration.ofMinutes(4)), ttl))
        assertFalse(isFresh(t0, t0.plus(Duration.ofMinutes(5)), ttl))
    }
}
