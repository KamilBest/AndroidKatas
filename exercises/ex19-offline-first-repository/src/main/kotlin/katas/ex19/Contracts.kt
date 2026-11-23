package katas.ex19

import kotlinx.coroutines.flow.Flow
import java.time.Instant

// GIVEN: "Retrofit"
data class ClaimDto(val id: String, val title: String, val amount: String, val status: String)
interface ClaimsApi { suspend fun fetchClaims(): List<ClaimDto> }

// GIVEN: "Room"
data class ClaimEntity(val id: String, val title: String, val amountMinor: Long, val status: String)
interface ClaimsDao {
    fun observeAll(): Flow<List<ClaimEntity>>
    suspend fun replaceAll(claims: List<ClaimEntity>) // @Transaction: delete + insert
}
interface SyncMetaStore {
    suspend fun lastSync(): Instant?
    suspend fun setLastSync(time: Instant)
}

// GIVEN: domain
enum class ClaimStatus { SUBMITTED, APPROVED, PAID, REJECTED, UNKNOWN }
data class Claim(val id: String, val title: String, val amountMinor: Long, val status: ClaimStatus)

enum class DataError { Network, Unknown }
sealed interface RefreshResult {
    data object Skipped : RefreshResult
    data class Updated(val count: Int) : RefreshResult
    data class Failed(val error: DataError) : RefreshResult
}
