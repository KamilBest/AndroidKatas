package katas.ex18

import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

// Steps (each one has its own tests in StepsTest.kt)

/** Start of today and start of tomorrow in [zone], as Instants. */
fun todayRange(clock: Clock, zone: ZoneId): Pair<Instant, Instant> = TODO()

/** TAKEN when taken; MISSED when more than [missedAfter] has passed since the planned time; otherwise PENDING. */
fun doseStatus(dose: DoseEntity, now: Instant, missedAfter: Duration): DoseStatus = TODO()

/** Label "Ibuprom 200 mg", local time in [zone], status from doseStatus. */
fun DoseEntity.toDomain(now: Instant, zone: ZoneId, missedAfter: Duration): Dose = TODO()

class DoseRepository(
    private val dao: DoseDao,
    private val clock: Clock,
    private val zone: ZoneId = ZoneId.of("Europe/Warsaw"),
    private val missedAfter: Duration = Duration.ofHours(1),
) {
    /** Today's doses (in [zone]) as domain objects. */
    fun todayDoses(): Flow<List<Dose>> = TODO()

    /** Number of PENDING doses today. Emits only when the number changes. */
    fun pendingCount(): Flow<Int> = TODO()

    suspend fun take(id: String): TakeResult = TODO()
}
