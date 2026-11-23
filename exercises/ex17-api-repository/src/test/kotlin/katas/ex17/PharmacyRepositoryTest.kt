package katas.ex17

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import java.io.IOException
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

// GIVEN: do not change
class FakePharmacyApi : PharmacyApi {
    val responses = mutableMapOf<String, PharmacyDto>()
    var error: Exception? = null
    var gate: CompletableDeferred<Unit>? = null
    val calls = mutableListOf<String>()

    override suspend fun pharmacy(id: String): PharmacyDto {
        calls += id
        gate?.await()
        error?.let { throw it }
        return responses[id] ?: throw HttpError(404)
    }
}

class MutableClock(var now: Instant) : Clock() {
    override fun getZone(): ZoneId = ZoneOffset.UTC
    override fun withZone(zone: ZoneId?): Clock = this
    override fun instant(): Instant = now
}

class PharmacyRepositoryTest {
    private val api = FakePharmacyApi()
    private val clock = MutableClock(Instant.parse("2026-05-01T10:00:00Z"))
    private val repo = PharmacyRepository(api, clock, ttl = Duration.ofMinutes(5))
    private val dto = PharmacyDto("p1", "Linden Pharmacy", "08:00-20:00", 4.6)
    private val pharmacy = Pharmacy("p1", "Linden Pharmacy", LocalTime.of(8, 0), LocalTime.of(20, 0), 4.6)

    @Test fun `returns mapped pharmacy`() = runTest {
        api.responses["p1"] = dto
        assertEquals(Outcome.Success(pharmacy), repo.pharmacy("p1"))
    }

    @Test fun `maps errors to domain errors`() = runTest {
        assertEquals(Outcome.Failure(DataError.NotFound), repo.pharmacy("missing"))
        api.error = IOException("offline")
        assertEquals(Outcome.Failure(DataError.Offline), repo.pharmacy("p1"))
        api.error = HttpError(503)
        assertEquals(Outcome.Failure(DataError.Server), repo.pharmacy("p1"))
    }

    @Test fun `caches result until ttl passes`() = runTest {
        api.responses["p1"] = dto
        repo.pharmacy("p1")
        clock.now = clock.now.plus(Duration.ofMinutes(4))
        assertEquals(Outcome.Success(pharmacy), repo.pharmacy("p1"))
        assertEquals(1, api.calls.size, "second call within ttl should come from cache")
        clock.now = clock.now.plus(Duration.ofMinutes(1))
        repo.pharmacy("p1")
        assertEquals(2, api.calls.size, "after ttl the api is called again")
    }

    @Test fun `failures are not cached`() = runTest {
        api.error = IOException("offline")
        repo.pharmacy("p1")
        api.error = null
        api.responses["p1"] = dto
        assertEquals(Outcome.Success(pharmacy), repo.pharmacy("p1"))
    }

    @Test fun `concurrent requests for the same id share one call`() = runTest {
        api.responses["p1"] = dto
        api.responses["p2"] = dto.copy(id = "p2")
        val gate = CompletableDeferred<Unit>().also { api.gate = it }
        val results = listOf("p1", "p1", "p1", "p2").map { id -> async { repo.pharmacy(id) } }
        runCurrent()
        gate.complete(Unit)
        results.awaitAll()
        assertEquals(listOf("p1", "p2"), api.calls.sorted())
    }
}
