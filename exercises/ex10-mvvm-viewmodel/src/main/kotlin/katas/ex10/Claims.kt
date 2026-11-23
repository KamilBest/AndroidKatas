package katas.ex10

import kotlinx.coroutines.flow.Flow

// GIVEN
enum class ClaimStatus { OPEN, PAID }
data class Claim(val id: String, val title: String, val amountMinor: Long, val status: ClaimStatus)

interface ClaimsRepository {
    fun observeClaims(): Flow<List<Claim>>
    suspend fun refresh()
}

data class ClaimUi(val id: String, val title: String, val amount: String, val isPaid: Boolean)

data class ClaimsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val claims: List<ClaimUi> = emptyList(),
    val error: String? = null,
)

sealed interface ClaimsEvent {
    data class ShowSnackbar(val message: String) : ClaimsEvent
    data class OpenDetails(val id: String) : ClaimsEvent
}

/** 123450 gr → "1234,50 zł" (ready) */
fun formatPln(amountMinor: Long): String =
    java.math.BigDecimal.valueOf(amountMinor, 2).toPlainString().replace('.', ',') + " zł"
