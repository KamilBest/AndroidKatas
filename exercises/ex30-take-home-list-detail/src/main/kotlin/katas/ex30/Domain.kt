package katas.ex30

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// GIVEN: domain and repository (offline-first like in ex19, here only the interface)
data class Prescription(
    val id: String,
    val drug: String,
    val dose: String,
    val validUntil: LocalDate,
    val redeemed: Boolean,
)

interface PrescriptionsRepository {
    fun observe(): Flow<List<Prescription>>
    /** Throws when there is no network. Data in the database stays. */
    suspend fun refresh()
    fun observeById(id: String): Flow<Prescription?>
}

// GIVEN: screen models
enum class Badge { ACTIVE, REDEEMED, EXPIRED }
data class PrescriptionItem(val id: String, val title: String, val subtitle: String, val badge: Badge)

sealed interface ListUiState {
    data object Loading : ListUiState
    data object Empty : ListUiState
    data class Content(val items: List<PrescriptionItem>, val isRefreshing: Boolean = false) : ListUiState
    data class Error(val message: String) : ListUiState
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Found(val item: PrescriptionItem) : DetailUiState
    data object NotFound : DetailUiState
}
