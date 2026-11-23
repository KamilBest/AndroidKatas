package katas.ex18

import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalTime

// GIVEN: "Room". In a real app this is a @Dao; here it is an interface so the tests are fast.
data class DoseEntity(val id: String, val medicationName: String, val doseMg: Int, val scheduledAt: Instant, val takenAt: Instant?)

interface DoseDao {
    /** Doses in [from, to), ascending by time. Emits again after every table change. */
    fun observeBetween(from: Instant, to: Instant): Flow<List<DoseEntity>>
    /** Sets takenAt only when it was null. Returns the number of changed rows. */
    suspend fun markTaken(id: String, at: Instant): Int
}

// GIVEN: domain
enum class DoseStatus { TAKEN, PENDING, MISSED }
data class Dose(val id: String, val label: String, val time: LocalTime, val status: DoseStatus)

sealed interface TakeResult {
    data object Taken : TakeResult
    data object AlreadyTaken : TakeResult
}
