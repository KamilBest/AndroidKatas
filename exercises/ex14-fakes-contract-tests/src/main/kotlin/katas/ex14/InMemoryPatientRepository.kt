package katas.ex14

import kotlinx.coroutines.flow.Flow

class InMemoryPatientRepository : PatientRepository {
    override suspend fun save(patient: Patient): Unit = TODO()
    override suspend fun findById(id: String): Patient? = TODO()
    override suspend fun findByPesel(pesel: String): Patient? = TODO()
    override suspend fun delete(id: String): Unit = TODO()
    override fun observeAll(): Flow<List<Patient>> = TODO()
}
