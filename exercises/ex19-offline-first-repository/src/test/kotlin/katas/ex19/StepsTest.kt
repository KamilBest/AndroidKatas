package katas.ex19

import java.io.IOException
import java.time.Duration
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// GIVEN: tests for single steps. Start with them, then OfflineFirstClaimsRepositoryTest.
class StepsTest {
    private val dto = ClaimDto("1", "Visit", "123.45", "approved")

    @Test fun `dto amount becomes minor units`() {
        assertEquals(ClaimEntity("1", "Visit", 12345, "approved"), dto.toEntity())
        assertEquals(700, dto.copy(amount = "7").toEntity().amountMinor)
        assertEquals(5, dto.copy(amount = "0.05").toEntity().amountMinor)
    }

    @Test fun `entity status maps to domain, unknown is tolerated`() {
        assertEquals(Claim("1", "Visit", 12345, ClaimStatus.APPROVED), dto.toEntity().toDomain())
        assertEquals(ClaimStatus.PAID, ClaimEntity("1", "x", 1, "PAID").toDomain().status)
        assertEquals(ClaimStatus.UNKNOWN, ClaimEntity("1", "x", 1, "ON_HOLD_NEW").toDomain().status)
    }

    @Test fun `freshness`() {
        val now = Instant.parse("2026-05-01T10:00:00Z")
        val maxAge = Duration.ofMinutes(15)
        assertFalse(isFresh(null, now, maxAge), "never synced")
        assertTrue(isFresh(now.minus(Duration.ofMinutes(14)), now, maxAge))
        assertFalse(isFresh(now.minus(Duration.ofMinutes(15)), now, maxAge), "exactly maxAge is stale")
    }

    @Test fun `errors map to domain errors`() {
        assertEquals(DataError.Network, IOException("offline").toDataError())
        assertEquals(DataError.Unknown, IllegalStateException("parse").toDataError())
    }
}
