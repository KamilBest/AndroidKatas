package katas.ex13

// GIVEN: do not change. This test describes the target API after the refactoring.

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeBookingApi : BookingApi {
    val requests = mutableListOf<BookingRequest>()
    var failWith: Exception? = null
    override suspend fun reserve(request: BookingRequest) {
        failWith?.let { throw it }
        requests += request
    }
}

class FakeAnalytics : Analytics {
    val events = mutableListOf<Pair<String, Map<String, String>>>()
    override suspend fun track(event: String, params: Map<String, String>) {
        delay(2_000)
        events += event to params
    }
}

class BookingServiceTest {
    // Wednesday 2026-03-11, 08:00 UTC = 09:00 in Warsaw (CET)
    private val clock = Clock.fixed(Instant.parse("2026-03-11T08:00:00Z"), ZoneOffset.UTC)
    private val api = FakeBookingApi()
    private val analytics = FakeAnalytics()

    // appScope = the TestScope itself: runTest waits for the fire-and-forget work and advanceUntilIdle() runs it.
    // (backgroundScope would not work here: advanceUntilIdle ignores "background" work).
    private fun TestScope.service() = BookingService(clock, { "abc123-def" }, api, analytics, appScope = this)

    @Test fun `books a valid slot`() = runTest {
        val slot = LocalDateTime.parse("2026-03-11T11:00")
        val result = service().book("patient-1", slot)
        assertEquals(BookingResult.Booked("abc123-def", "WAW-ABC123"), result)
        assertEquals(listOf(BookingRequest("abc123-def", "patient-1", slot)), api.requests)
    }

    @Test fun `two hours notice is computed in clinic time zone`() = runTest {
        assertEquals(BookingResult.TooLate, service().book("p", LocalDateTime.parse("2026-03-11T10:59")))
        assertTrue(service().book("p", LocalDateTime.parse("2026-03-11T11:00")) is BookingResult.Booked)
    }

    @Test fun `weekend is closed`() = runTest {
        assertEquals(BookingResult.ClinicClosed, service().book("p", LocalDateTime.parse("2026-03-14T10:00")))
        assertTrue(api.requests.isEmpty())
    }

    @Test fun `network error is retryable`() = runTest {
        api.failWith = IOException("timeout")
        assertEquals(BookingResult.Failed(retryable = true), service().book("p", LocalDateTime.parse("2026-03-12T10:00")))
    }

    @Test fun `other errors are not retryable`() = runTest {
        api.failWith = IllegalStateException("409")
        assertEquals(BookingResult.Failed(retryable = false), service().book("p", LocalDateTime.parse("2026-03-12T10:00")))
        assertTrue(analytics.events.isEmpty())
    }

    @Test fun `analytics is fire and forget`() = runTest {
        service().book("p", LocalDateTime.parse("2026-03-12T10:00"))
        assertTrue(analytics.events.isEmpty(), "booking must not wait for analytics")
        advanceUntilIdle()
        assertEquals(listOf("booking_created" to mapOf("id" to "abc123-def")), analytics.events)
    }
}
