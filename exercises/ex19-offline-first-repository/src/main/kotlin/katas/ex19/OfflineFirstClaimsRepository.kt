package katas.ex19

import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.Duration
import java.time.Instant

// Steps (each one has its own tests in StepsTest.kt)

/** "123.45" → 12345 grosze. */
fun ClaimDto.toEntity(): ClaimEntity = TODO()

/** An unknown status (e.g. a new one from the backend) → UNKNOWN. */
fun ClaimEntity.toDomain(): Claim = TODO()

/** Whether data synced at [lastSync] is fresh at [now]. null = never synced. */
fun isFresh(lastSync: Instant?, now: Instant, maxAge: Duration): Boolean = TODO()

/** IOException → Network, any other error → Unknown. */
fun Throwable.toDataError(): DataError = TODO()

class OfflineFirstClaimsRepository(
    private val api: ClaimsApi,
    private val dao: ClaimsDao,
    private val meta: SyncMetaStore,
    private val clock: Clock,
    private val maxAge: Duration = Duration.ofMinutes(15),
) {
    fun observeClaims(): Flow<List<Claim>> = TODO()

    suspend fun refresh(force: Boolean = false): RefreshResult = TODO()
}
