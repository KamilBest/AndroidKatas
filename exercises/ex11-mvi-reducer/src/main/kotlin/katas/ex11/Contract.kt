package katas.ex11

// GIVEN: screen contract
data class Doctor(val id: String, val name: String)
enum class Step { PICK_DOCTOR, PICK_SLOT }

data class BookingState(
    val step: Step = Step.PICK_DOCTOR,
    val doctors: List<Doctor> = emptyList(),
    val selectedDoctorId: String? = null,
    val slots: List<String> = emptyList(),
    val selectedSlot: String? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
)

sealed interface Intent {
    data object LoadDoctors : Intent
    data class DoctorsLoaded(val doctors: List<Doctor>) : Intent
    data class LoadFailed(val message: String) : Intent
    data class SelectDoctor(val id: String) : Intent
    data class SlotsLoaded(val doctorId: String, val slots: List<String>) : Intent
    data class SelectSlot(val slot: String) : Intent
    data object Confirm : Intent
    data class Booked(val confirmationCode: String) : Intent
    data class BookingFailed(val message: String) : Intent
    data object Back : Intent
}

sealed interface Effect {
    data object FetchDoctors : Effect
    data class FetchSlots(val doctorId: String) : Effect
    data class Book(val doctorId: String, val slot: String) : Effect
    sealed interface Ui : Effect
    data class ShowConfirmation(val code: String) : Ui
    data object Close : Ui
}

data class Next(val state: BookingState, val effects: List<Effect> = emptyList())

interface DoctorsApi {
    suspend fun doctors(): List<Doctor>
    suspend fun slots(doctorId: String): List<String>
    suspend fun book(doctorId: String, slot: String): String
}
