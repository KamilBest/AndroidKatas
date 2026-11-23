package katas.bonus05

import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

// GIVEN
sealed interface DosePlan {
    val startDate: LocalDate
    val endDate: LocalDate?

    data class TimesOfDay(val times: List<LocalTime>, override val startDate: LocalDate, override val endDate: LocalDate? = null) : DosePlan
    data class EveryNHours(val hours: Long, val firstDose: LocalTime, override val startDate: LocalDate, override val endDate: LocalDate? = null) : DosePlan
    data class Weekly(val days: Set<DayOfWeek>, val time: LocalTime, override val startDate: LocalDate, override val endDate: LocalDate? = null) : DosePlan
}

class DoseScheduler(private val zone: ZoneId) {
    fun dosesBetween(plan: DosePlan, from: LocalDate, to: LocalDate): List<Instant> = TODO()
    fun nextDose(plan: DosePlan, now: Instant): Instant? = TODO()
    fun missedDoses(plan: DosePlan, taken: Set<Instant>, now: Instant, grace: Duration = Duration.ofMinutes(60)): List<Instant> = TODO()
}
