package katas.ex18

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

// GIVEN: tests for single steps. Start with them, then DoseRepositoryTest.
class StepsTest {
    private val warsaw = ZoneId.of("Europe/Warsaw")
    private fun at(local: String): Instant = LocalDateTime.parse(local).atZone(warsaw).toInstant()
    private fun entity(scheduled: String, taken: String? = null) = DoseEntity("d1", "Ibuprom", 200, at(scheduled), taken?.let(::at))

    @Test fun `today range uses patient zone`() {
        // 23:30 UTC on 31 May is already 1 June in Warsaw
        val clock = Clock.fixed(Instant.parse("2026-05-31T23:30:00Z"), ZoneOffset.UTC)
        assertEquals(at("2026-06-01T00:00") to at("2026-06-02T00:00"), todayRange(clock, warsaw))
    }

    @Test fun `today range on DST change day is 23 hours`() {
        val clock = Clock.fixed(Instant.parse("2026-03-29T10:00:00Z"), ZoneOffset.UTC)
        val (from, to) = todayRange(clock, warsaw)
        assertEquals(Duration.ofHours(23), Duration.between(from, to))
    }

    @Test fun `dose status`() {
        val now = at("2026-06-01T10:00")
        val hour = Duration.ofHours(1)
        assertEquals(DoseStatus.TAKEN, doseStatus(entity("2026-06-01T07:00", taken = "2026-06-01T07:05"), now, hour))
        assertEquals(DoseStatus.MISSED, doseStatus(entity("2026-06-01T08:59"), now, hour))
        assertEquals(DoseStatus.PENDING, doseStatus(entity("2026-06-01T09:00"), now, hour), "exactly 1 h late is still pending")
        assertEquals(DoseStatus.PENDING, doseStatus(entity("2026-06-01T20:00"), now, hour))
    }

    @Test fun `maps entity to domain`() {
        val now = at("2026-06-01T10:00")
        assertEquals(
            Dose("d1", "Ibuprom 200 mg", LocalTime.of(20, 15), DoseStatus.PENDING),
            entity("2026-06-01T20:15").toDomain(now, warsaw, Duration.ofHours(1)),
        )
    }
}
