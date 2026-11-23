package katas.bonus05

import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DoseSchedulerTest {
    private val warsaw = ZoneId.of("Europe/Warsaw")
    private val scheduler = DoseScheduler(warsaw)
    private fun d(s: String) = LocalDate.parse(s)
    private fun t(s: String) = LocalTime.parse(s)
    private fun i(s: String) = Instant.parse(s)
    private fun localTimes(instants: List<Instant>) = instants.map { it.atZone(warsaw).toLocalDateTime().toString() }

    @Test fun `times of day on a normal winter day`() {
        val plan = DosePlan.TimesOfDay(listOf(t("20:00"), t("08:00")), startDate = d("2026-01-01"))
        assertEquals(listOf(i("2026-03-10T07:00:00Z"), i("2026-03-10T19:00:00Z")), scheduler.dosesBetween(plan, d("2026-03-10"), d("2026-03-10")))
    }

    @Test fun `wall clock time in DST gap moves after the gap`() {
        val plan = DosePlan.TimesOfDay(listOf(t("02:30")), startDate = d("2026-01-01"))
        assertEquals(
            listOf("2026-03-28T02:30", "2026-03-29T03:30", "2026-03-30T02:30"),
            localTimes(scheduler.dosesBetween(plan, d("2026-03-28"), d("2026-03-30"))),
        )
    }

    @Test fun `wall clock time in DST overlap happens once`() {
        val plan = DosePlan.TimesOfDay(listOf(t("02:30")), startDate = d("2026-01-01"))
        assertEquals(listOf(i("2026-10-25T00:30:00Z")), scheduler.dosesBetween(plan, d("2026-10-25"), d("2026-10-25")))
    }

    @Test fun `every 8 hours is elapsed time and shifts the wall clock across DST`() {
        val plan = DosePlan.EveryNHours(8, t("08:00"), startDate = d("2026-03-28"))
        assertEquals(
            listOf("2026-03-28T08:00", "2026-03-28T16:00", "2026-03-29T00:00", "2026-03-29T09:00", "2026-03-29T17:00"),
            localTimes(scheduler.dosesBetween(plan, d("2026-03-28"), d("2026-03-29"))),
        )
        val doses = scheduler.dosesBetween(plan, d("2026-03-28"), d("2026-03-29"))
        doses.zipWithNext().forEach { (a, b) -> assertEquals(Duration.ofHours(8), Duration.between(a, b)) }
    }

    @Test fun `every N hours window starts mid-plan`() {
        val plan = DosePlan.EveryNHours(12, t("09:00"), startDate = d("2026-05-01"))
        assertEquals(listOf("2026-05-03T09:00", "2026-05-03T21:00"), localTimes(scheduler.dosesBetween(plan, d("2026-05-03"), d("2026-05-03"))))
    }

    @Test fun `weekly plan and start-end bounds`() {
        val plan = DosePlan.Weekly(setOf(DayOfWeek.MONDAY, DayOfWeek.THURSDAY), t("09:00"), startDate = d("2026-06-02"), endDate = d("2026-06-11"))
        assertEquals(
            listOf("2026-06-04T09:00", "2026-06-08T09:00", "2026-06-11T09:00"),
            localTimes(scheduler.dosesBetween(plan, d("2026-06-01"), d("2026-06-30"))),
        )
        assertEquals(emptyList(), scheduler.dosesBetween(plan, d("2026-07-01"), d("2026-07-31")))
    }

    @Test fun `next dose`() {
        val plan = DosePlan.TimesOfDay(listOf(t("08:00"), t("20:00")), startDate = d("2026-01-01"))
        assertEquals(i("2026-03-10T19:00:00Z"), scheduler.nextDose(plan, i("2026-03-10T07:00:00Z")), "strictly after now")
        assertEquals(i("2026-03-11T07:00:00Z"), scheduler.nextDose(plan, i("2026-03-10T21:00:00Z")))
        val ended = plan.copy(endDate = d("2026-03-10"))
        assertNull(scheduler.nextDose(ended, i("2026-03-10T21:00:00Z")))
    }

    @Test fun `missed doses respect grace period and taken set`() {
        val plan = DosePlan.TimesOfDay(listOf(t("08:00"), t("12:00"), t("20:00")), startDate = d("2026-01-01"))
        val now = i("2026-03-10T11:30:00Z") // 12:30 local time
        assertEquals(listOf(i("2026-03-10T07:00:00Z")), scheduler.missedDoses(plan, emptySet(), now))
        assertEquals(emptyList(), scheduler.missedDoses(plan, setOf(i("2026-03-10T07:00:00Z")), now))
        assertEquals(2, scheduler.missedDoses(plan, emptySet(), now, grace = Duration.ofMinutes(15)).size)
    }
}
