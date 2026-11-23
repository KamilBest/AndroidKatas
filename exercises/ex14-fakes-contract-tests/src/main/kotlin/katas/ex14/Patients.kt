package katas.ex14

import kotlinx.coroutines.flow.Flow

// GIVEN
data class Patient(val id: String, val pesel: String, val name: String)

interface PatientRepository {
    suspend fun save(patient: Patient)                 // insert or update by id
    suspend fun findById(id: String): Patient?
    suspend fun findByPesel(pesel: String): Patient?
    suspend fun delete(id: String)                     // missing = no-op
    fun observeAll(): Flow<List<Patient>>              // sorted by name, emits after every change
}
