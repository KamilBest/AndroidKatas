package katas.ex15

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

// GIVEN
data class VisitRequest(val doctorId: String, val symptoms: String, val sinceDays: Int, val urgent: Boolean)
interface VisitApi { suspend fun requestVisit(request: VisitRequest): String }

data class VisitFormState(
    val symptoms: String = "",
    val sinceDays: Int? = null,
    val urgent: Boolean = false,
    val symptomsError: Boolean = false,
    val sinceDaysError: Boolean = false,
    val canSubmit: Boolean = false,
    val isSubmitting: Boolean = false,
)

sealed interface VisitEvent {
    data class Submitted(val visitId: String) : VisitEvent
    data class Error(val message: String) : VisitEvent
}

class VisitFormViewModel(private val handle: SavedStateHandle, private val api: VisitApi) : ViewModel() {

    val doctorId: String = TODO()
    val state: StateFlow<VisitFormState> = TODO()
    val events: Flow<VisitEvent> = TODO()

    fun onSymptomsChange(value: String): Unit = TODO()
    fun onSinceDaysChange(value: Int?): Unit = TODO()
    fun onUrgentChange(value: Boolean): Unit = TODO()
    fun submit(): Unit = TODO()
}
