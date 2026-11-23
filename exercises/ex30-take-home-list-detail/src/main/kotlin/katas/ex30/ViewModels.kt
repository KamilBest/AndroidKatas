package katas.ex30

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.time.Clock
import java.time.LocalDate

// Steps (each one has its own tests in StepsTest.kt)

/** Today's date in Europe/Warsaw. */
fun todayInWarsaw(clock: Clock): LocalDate = TODO()

/** REDEEMED when redeemed; EXPIRED when validUntil < today; otherwise ACTIVE. */
fun badgeFor(prescription: Prescription, today: LocalDate): Badge = TODO()

/** Prescription → list row: "Amotaks 1000 mg", "Valid until 12.03.2026", badge. */
fun Prescription.toItem(clock: Clock): PrescriptionItem = TODO()

/** List order: active (nearest date first), redeemed, expired. */
fun sortedForList(prescriptions: List<Prescription>, today: LocalDate): List<Prescription> = TODO()

/** Refresh state kept by the list ViewModel. */
data class RefreshStatus(val initialLoad: Boolean = true, val refreshing: Boolean = false, val failed: Boolean = false)

/** A pure function: database data + refresh state → screen state. */
fun listStateFor(prescriptions: List<Prescription>, refresh: RefreshStatus, clock: Clock): ListUiState = TODO()

class PrescriptionListViewModel(
    private val repository: PrescriptionsRepository,
    private val clock: Clock,
) : ViewModel() {
    val state: StateFlow<ListUiState> = TODO()
    /** One-time messages (snackbar). */
    val messages: Flow<String> = TODO()

    fun onRefresh(): Unit = TODO()
}

class PrescriptionDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PrescriptionsRepository,
    private val clock: Clock,
) : ViewModel() {
    val state: StateFlow<DetailUiState> = TODO()
}
