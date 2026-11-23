package katas.ex19

import app.cash.turbine.test
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import java.io.IOException
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeDao : ClaimsDao {
    val table = MutableStateFlow<List<ClaimEntity>>(emptyList())
    override fun observeAll() = table
    override suspend fun replaceAll(claims: List<ClaimEntity>) { table.value = claims }
}

class FakeApi : ClaimsApi {
    var response: List<ClaimDto> = emptyList()
    var error: Exception? = null
    var calls = 0
    override suspend fun fetchClaims(): List<ClaimDto> {
        calls++
        delay(200)
        error?.let { throw it }
        return response
    }
}

class FakeMeta : SyncMetaStore {
    var last: Instant? = null
    override suspend fun lastSync() = last
    override suspend fun setLastSync(time: Instant) { last = time }
}

/** A clock you can move forward in a test. */
class MutableClock(var now: Instant) : Clock() {
    override fun getZone() = ZoneOffset.UTC
    override fun withZone(zone: java.time.ZoneId?) = this
    override fun instant() = now
}

class OfflineFirstClaimsRepositoryTest {
    private val api = FakeApi()
    private val dao = FakeDao()
    private val meta = FakeMeta()
    private val clock = MutableClock(Instant.parse("2026-05-01T10:00:00Z"))
    private val repo = OfflineFirstClaimsRepository(api, dao, meta, clock, maxAge = Duration.ofMinutes(15))

    private val dto = ClaimDto("1", "Visit", "123.45", "approved")

    @Test fun `refresh writes to db and ui observes db`() = runTest {
        api.response = listOf(dto)
        repo.observeClaims().test {
            assertEquals(emptyList(), awaitItem())
            assertEquals(RefreshResult.Updated(1), repo.refresh())
            assertEquals(listOf(Claim("1", "Visit", 12345, ClaimStatus.APPROVED)), awaitItem())
        }
        assertEquals(clock.now, meta.last)
    }

    @Test fun `fresh cache skips network unless forced`() = runTest {
        meta.last = clock.now.minus(Duration.ofMinutes(14))
        assertEquals(RefreshResult.Skipped, repo.refresh())
        assertEquals(0, api.calls)
        assertEquals(RefreshResult.Updated(0), repo.refresh(force = true))
        assertEquals(1, api.calls)
    }

    @Test fun `stale cache refreshes`() = runTest {
        meta.last = clock.now.minus(Duration.ofMinutes(15))
        assertEquals(RefreshResult.Updated(0), repo.refresh())
    }

    @Test fun `errors are mapped and db is untouched`() = runTest {
        dao.table.value = listOf(ClaimEntity("old", "Old", 1, "paid"))
        val cases = mapOf(
            IOException("offline") to DataError.Network,
            IllegalStateException("parse") to DataError.Unknown,
        )
        for ((error, expected) in cases) {
            api.error = error
            assertEquals(RefreshResult.Failed(expected), repo.refresh(force = true), "for $error")
        }
        assertEquals("old", dao.table.value.single().id)
    }

    @Test fun `concurrent refreshes hit network once`() = runTest {
        api.response = listOf(dto)
        val results = List(3) { async { repo.refresh() } }.awaitAll()
        assertEquals(1, api.calls)
        assertEquals(1, results.count { it is RefreshResult.Updated })
        assertEquals(2, results.count { it == RefreshResult.Skipped })
    }
}
