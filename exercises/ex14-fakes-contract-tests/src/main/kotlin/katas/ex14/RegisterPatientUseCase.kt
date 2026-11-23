package katas.ex14

sealed interface RegisterResult {
    data class Registered(val patient: Patient) : RegisterResult
    data object PeselTaken : RegisterResult
    data object InvalidName : RegisterResult
}

/** Registers a patient: the name after trim must not be empty, PESEL is unique, id comes from [newId]. */
class RegisterPatientUseCase(private val repository: PatientRepository, private val newId: () -> String) {
    suspend operator fun invoke(pesel: String, name: String): RegisterResult = TODO()
}
