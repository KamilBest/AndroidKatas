package katas.ex22

import androidx.room.Dao
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
abstract class MedicationDao {
    open suspend fun upsertMedication(medication: MedicationEntity): Unit = TODO()
    open fun observeMedications(): Flow<List<MedicationEntity>> = TODO()
    open suspend fun insertDoses(doses: List<DoseEntity>): Unit = TODO()
    open fun observeDosesBetween(from: Instant, to: Instant): Flow<List<DoseEntity>> = TODO()
    open suspend fun markTaken(doseId: String, at: Instant): Int = TODO()

    /** Deletes future (scheduledAt > now), not taken doses of the medication and inserts [doses], atomically. */
    open suspend fun replaceFutureDoses(medicationId: String, now: Instant, doses: List<DoseEntity>): Unit = TODO()
}
