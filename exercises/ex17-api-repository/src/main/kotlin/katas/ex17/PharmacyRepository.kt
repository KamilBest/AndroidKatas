package katas.ex17

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalTime

// Steps (each one has its own tests in StepsTest.kt)

/** "08:00-20:00" → (08:00, 20:00). null or a bad format → null. */
fun parseOpeningHours(text: String?): Pair<LocalTime, LocalTime>? = TODO()

/** Use parseOpeningHours. */
fun PharmacyDto.toDomain(): Pharmacy = TODO()

/** 404 → NotFound, any other code → Server. */
fun HttpError.toDataError(): DataError = TODO()

/** Whether an entry fetched at [fetchedAt] is still valid at [now]. */
fun isFresh(fetchedAt: Instant, now: Instant, ttl: Duration): Boolean = TODO()

class PharmacyRepository(
    private val api: PharmacyApi,
    private val clock: Clock,
    private val ttl: Duration = Duration.ofMinutes(5),
) {
    suspend fun pharmacy(id: String): Outcome<Pharmacy> = TODO()
}
