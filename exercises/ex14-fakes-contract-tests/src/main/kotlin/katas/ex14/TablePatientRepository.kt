package katas.ex14

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** The "production" implementation (pretends to be an SQL table with an invalidation tracker). It has a bug: find it with the contract. */
class TablePatientRepository : PatientRepository {
    private val rows = linkedMapOf<String, Patient>()
    private val version = MutableStateFlow(0)

    override suspend fun save(patient: Patient) {
        rows[patient.id] = patient
        version.value++
    }

    override suspend fun findById(id: String): Patient? = rows[id]

    override suspend fun findByPesel(pesel: String): Patient? = rows.values.firstOrNull { it.pesel == pesel }

    override suspend fun delete(id: String) {
        rows.remove(id)
    }

    override fun observeAll(): Flow<List<Patient>> = version.map { rows.values.sortedBy { it.name } }
}
